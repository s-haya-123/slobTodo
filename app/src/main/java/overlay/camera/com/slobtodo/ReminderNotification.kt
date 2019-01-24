package overlay.camera.com.slobtodo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.app.PendingIntent
import android.graphics.Color
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


class ReminderNotification: BroadcastReceiver() {
    companion object {
        val REMINDER_REQUESTCODE = 0
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmBroadcastReceiver", "onReceive() pid=" + android.os.Process.myPid())

        val requestCode = intent?.getIntExtra("RequestCode", 0)

        val pendingIntent = PendingIntent.getActivity(context, requestCode!!, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = "default"
        // app name
        val title = context?.getString(R.string.app_name)

        val currentTime = System.currentTimeMillis()
        val dataFormat = SimpleDateFormat("HH:mm:ss", Locale.JAPAN)
        val cTime = dataFormat.format(currentTime)

        // メッセージ　+ 11:22:331
        val message = "時間になりました。 $cTime"

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Notification　Channel 設定
        val channel:NotificationChannel = NotificationChannel(
                channelId, title, NotificationManager.IMPORTANCE_DEFAULT).apply {
            this.description = message
            this.enableVibration(true)
            this.canShowBadge()
            this.enableLights(true)
            this.lightColor = Color.BLUE
            // the channel appears on the lockscreen
            this.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            this.setSound(defaultSoundUri, null)
            this.setShowBadge(true)
        }


        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel)

            val notification:Notification = Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    // android標準アイコンから
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build()

            // 通知
            notificationManager.notify(R.string.app_name, notification)

        }
    }
    private fun exampleNotification(context: Context?,intent: Intent?){
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("default",context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT).let {
            it.description = "test"
            it.enableVibration(true)
        }
        val notification = Notification.Builder(context,"default").let {
            it.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            it.setContentText("test")
            it.build()
        }
        notificationManager.notify(R.string.app_name,notification)

    }
}