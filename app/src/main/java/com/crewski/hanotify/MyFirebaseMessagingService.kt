package com.crewski.hanotify

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.support.v4.app.NotificationCompat
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.app.NotificationChannel
import android.os.Build
import android.graphics.Color
import android.util.Log
import android.util.Log.d
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // ...
        // Check if message contains a data payload.


        var title = ""
        var message = ""
        var color = ""
//        var sensor = ""
        var actions: JSONArray = JSONArray()

        val dataJSON = JSONObject(remoteMessage!!.data);

        Log.d("Message", dataJSON.toString())

        if (dataJSON.has("title")) {
            title = dataJSON.getString("title")// handler
        }
        if (dataJSON.has("body")) {
            message = dataJSON.getString("body")// handler
        }
        if (dataJSON.has("color")) {
            color = dataJSON.getString("color")// handler
        }
//        if (dataJSON.has("sensor")) {
//            sensor = dataJSON.getString("sensor")// handler
//        }
        if (dataJSON.has("actions")) {
            actions = JSONArray(dataJSON.getString("actions"))// handler
        }




        sendNotification(message, title, color, actions)

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String, title: String, color: String, actions: JSONArray) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val notification_id = (353..37930).random()
        d("Notification_ID", notification_id.toString())
        val channelId = "HomeAssistant"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(Color.parseColor(color))


        for (i in 0 until 3) {
                try {
                    val oneObject = actions.getJSONObject(i)
                    val title = oneObject.getString("title")
                    val action = oneObject.getString("action")
                    val broadcastIntent = Intent(this, ResponseReceiver::class.java)
//                    broadcastIntent.putExtra("sensor", sensor)
                    broadcastIntent.putExtra("id", notification_id)
                    broadcastIntent.putExtra("action", action)
                    val actionIntent = PendingIntent.getBroadcast(this, (353..37930).random(), broadcastIntent, 0)
                    notificationBuilder.addAction(R.drawable.notification_icon, title, actionIntent)

                } catch (e: JSONException) {
                    // Oops
                }
        }









        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notification_id /* ID of notification */, notificationBuilder.build())
    }

    fun ClosedRange<Int>.random() =
            Random().nextInt((endInclusive + 1) - start) +  start
}


