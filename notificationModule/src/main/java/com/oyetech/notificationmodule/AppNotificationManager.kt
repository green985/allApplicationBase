package com.oyetech.notificationmodule

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import android.app.NotificationManager as AndroidNotificationManager

class AppNotificationManager(private val context: Context) {

    // Sistem NotificationManager servisini alıyoruz.
    private val notificationManager: AndroidNotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager

    /**
     * Bildirim kanalı oluşturur. Android O (API 26) ve üzeri için kullanılır.
     *
     * @param channelId Kanalın benzersiz kimliği.
     * @param channelName Kullanıcıya gösterilecek kanal adı.
     * @param importance Kanalın önem derecesi (NotificationManager.IMPORTANCE_XXX).
     */
    fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Bildirim oluşturup gösterir.
     *
     * @param title Bildirim başlığı.
     * @param message Bildirim mesajı.
     * @param channelId Kullanılacak bildirim kanalının kimliği.
     * @param notificationId Bildirimin benzersiz ID'si. Belirtilmezse, sistem zaman damgası kullanılır.
     */
    fun showNotification(
        title: String,
        message: String,
        channelId: String,
        notificationId: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Uygulamanıza uygun ikon ile değiştirin.
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Belirtilen ID'ye sahip bildirimi iptal eder.
     *
     * @param notificationId İptal edilecek bildirimin ID'si.
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    /**
     * Uygulamadaki tüm bildirimleri iptal eder.
     */
    fun cancelAll() {
        notificationManager.cancelAll()
    }
}
