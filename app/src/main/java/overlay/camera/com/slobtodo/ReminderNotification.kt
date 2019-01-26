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
import java.text.SimpleDateFormat
import java.util.*


class ReminderNotification: BroadcastReceiver() {
    companion object {
        val REMINDER_REQUESTCODE = 0
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        val channelId = "default"
        val title:String? = context?.getString(R.string.app_name)

        val currentTime = System.currentTimeMillis()
        val dataFormat = SimpleDateFormat("HH:mm:ss", Locale.JAPAN)
        val cTime = dataFormat.format(currentTime)

        val message = "時間になりました。 $cTime"
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = createNotification(context!!,intent!!,notificationManager,channelId,title!!,message)
        notificationManager.notify(R.string.app_name, notification)

        }
    fun createNotification(context:Context,intent:Intent,notificationManager:NotificationManager,channelId:String,title:String,message:String):Notification? {
        val requestCode = intent.getIntExtra("RequestCode", 0)
        val pendingIntent = PendingIntent.getActivity(context, requestCode!!, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channel = NotificationChannel(
                channelId, title, NotificationManager.IMPORTANCE_DEFAULT).apply {
            this.description = message
            this.enableVibration(true)
            this.canShowBadge()
            this.enableLights(true)
            this.lightColor = Color.BLUE
            this.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            this.setSound(defaultSoundUri, null)
            this.setShowBadge(true)
        }

        notificationManager.createNotificationChannel(channel)

        return Notification.Builder(context, channelId)
                .setContentTitle(title)
                // android標準アイコンから
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .build()
    }

}