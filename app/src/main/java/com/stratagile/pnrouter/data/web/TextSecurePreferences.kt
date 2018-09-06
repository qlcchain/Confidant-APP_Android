package com.stratagile.pnrouter.data.web

import android.content.Context
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.annotation.ArrayRes
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.utils.VersionUtil
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.security.SecureRandom
import java.util.*

object TextSecurePreferences {

    private val TAG = TextSecurePreferences::class.java.simpleName

    val IDENTITY_PREF = "pref_choose_identity"
    val CHANGE_PASSPHRASE_PREF = "pref_change_passphrase"
    val DISABLE_PASSPHRASE_PREF = "pref_disable_passphrase"
    val THEME_PREF = "pref_theme"
    val LANGUAGE_PREF = "pref_language"
    private val MMSC_CUSTOM_HOST_PREF = "pref_apn_mmsc_custom_host"
    val MMSC_HOST_PREF = "pref_apn_mmsc_host"
    private val MMSC_CUSTOM_PROXY_PREF = "pref_apn_mms_custom_proxy"
    val MMSC_PROXY_HOST_PREF = "pref_apn_mms_proxy"
    private val MMSC_CUSTOM_PROXY_PORT_PREF = "pref_apn_mms_custom_proxy_port"
    val MMSC_PROXY_PORT_PREF = "pref_apn_mms_proxy_port"
    private val MMSC_CUSTOM_USERNAME_PREF = "pref_apn_mmsc_custom_username"
    val MMSC_USERNAME_PREF = "pref_apn_mmsc_username"
    private val MMSC_CUSTOM_PASSWORD_PREF = "pref_apn_mmsc_custom_password"
    val MMSC_PASSWORD_PREF = "pref_apn_mmsc_password"
    val THREAD_TRIM_LENGTH = "pref_trim_length"
    val THREAD_TRIM_NOW = "pref_trim_now"
    val ENABLE_MANUAL_MMS_PREF = "pref_enable_manual_mms"

    private val LAST_VERSION_CODE_PREF = "last_version_code"
    private val LAST_EXPERIENCE_VERSION_PREF = "last_experience_version_code"
    private val EXPERIENCE_DISMISSED_PREF = "experience_dismissed"
    val RINGTONE_PREF = "pref_key_ringtone"
    val VIBRATE_PREF = "pref_key_vibrate"
    private val NOTIFICATION_PREF = "pref_key_enable_notifications"
    val LED_COLOR_PREF = "pref_led_color"
    val LED_BLINK_PREF = "pref_led_blink"
    private val LED_BLINK_PREF_CUSTOM = "pref_led_blink_custom"
    val ALL_MMS_PREF = "pref_all_mms"
    val ALL_SMS_PREF = "pref_all_sms"
    val PASSPHRASE_TIMEOUT_INTERVAL_PREF = "pref_timeout_interval"
    val PASSPHRASE_TIMEOUT_PREF = "pref_timeout_passphrase"
    val SCREEN_SECURITY_PREF = "pref_screen_security"
    private val ENTER_SENDS_PREF = "pref_enter_sends"
    private val ENTER_PRESENT_PREF = "pref_enter_key"
    private val SMS_DELIVERY_REPORT_PREF = "pref_delivery_report_sms"
    val MMS_USER_AGENT = "pref_mms_user_agent"
    private val MMS_CUSTOM_USER_AGENT = "pref_custom_mms_user_agent"
    private val THREAD_TRIM_ENABLED = "pref_trim_threads"
    private val LOCAL_NUMBER_PREF = "pref_local_number"
    private val VERIFYING_STATE_PREF = "pref_verifying"
    val REGISTERED_GCM_PREF = "pref_gcm_registered"
    private val GCM_PASSWORD_PREF = "pref_gcm_password"
    private val PROMPTED_PUSH_REGISTRATION_PREF = "pref_prompted_push_registration"
    private val PROMPTED_DEFAULT_SMS_PREF = "pref_prompted_default_sms"
    private val PROMPTED_OPTIMIZE_DOZE_PREF = "pref_prompted_optimize_doze"
    private val PROMPTED_SHARE_PREF = "pref_prompted_share"
    private val SIGNALING_KEY_PREF = "pref_signaling_key"
    private val DIRECTORY_FRESH_TIME_PREF = "pref_directory_refresh_time"
    private val UPDATE_APK_REFRESH_TIME_PREF = "pref_update_apk_refresh_time"
    private val UPDATE_APK_DOWNLOAD_ID = "pref_update_apk_download_id"
    private val UPDATE_APK_DIGEST = "pref_update_apk_digest"
    private val SIGNED_PREKEY_ROTATION_TIME_PREF = "pref_signed_pre_key_rotation_time"
    private val IN_THREAD_NOTIFICATION_PREF = "pref_key_inthread_notifications"
    private val SHOW_INVITE_REMINDER_PREF = "pref_show_invite_reminder"
    val MESSAGE_BODY_TEXT_SIZE_PREF = "pref_message_body_text_size"

    private val LOCAL_REGISTRATION_ID_PREF = "pref_local_registration_id"
    private val SIGNED_PREKEY_REGISTERED_PREF = "pref_signed_prekey_registered"
    private val WIFI_SMS_PREF = "pref_wifi_sms"

    private val GCM_DISABLED_PREF = "pref_gcm_disabled"
    private val GCM_REGISTRATION_ID_PREF = "pref_gcm_registration_id"
    private val GCM_REGISTRATION_ID_VERSION_PREF = "pref_gcm_registration_id_version"
    private val GCM_REGISTRATION_ID_TIME_PREF = "pref_gcm_registration_id_last_set_time"
    private val WEBSOCKET_REGISTERED_PREF = "pref_websocket_registered"
    private val RATING_LATER_PREF = "pref_rating_later"
    private val RATING_ENABLED_PREF = "pref_rating_enabled"
    private val SIGNED_PREKEY_FAILURE_COUNT_PREF = "pref_signed_prekey_failure_count"

    val REPEAT_ALERTS_PREF = "pref_repeat_alerts"
    val NOTIFICATION_PRIVACY_PREF = "pref_notification_privacy"
    val NOTIFICATION_PRIORITY_PREF = "pref_notification_priority"
    val NEW_CONTACTS_NOTIFICATIONS = "pref_enable_new_contacts_notifications"
    val WEBRTC_CALLING_PREF = "pref_webrtc_calling"

    val MEDIA_DOWNLOAD_MOBILE_PREF = "pref_media_download_mobile"
    val MEDIA_DOWNLOAD_WIFI_PREF = "pref_media_download_wifi"
    val MEDIA_DOWNLOAD_ROAMING_PREF = "pref_media_download_roaming"

    val SYSTEM_EMOJI_PREF = "pref_system_emoji"
    private val MULTI_DEVICE_PROVISIONED_PREF = "pref_multi_device"
    val DIRECT_CAPTURE_CAMERA_ID = "pref_direct_capture_camera_id"
    private val ALWAYS_RELAY_CALLS_PREF = "pref_turn_only"
    private val PROFILE_KEY_PREF = "pref_profile_key"
    private val PROFILE_NAME_PREF = "pref_profile_name"
    private val PROFILE_AVATAR_ID_PREF = "pref_profile_avatar_id"
    val READ_RECEIPTS_PREF = "pref_read_receipts"
    val INCOGNITO_KEYBORAD_PREF = "pref_incognito_keyboard"
    private val UNAUTHORIZED_RECEIVED = "pref_unauthorized_received"
    private val SUCCESSFUL_DIRECTORY_PREF = "pref_successful_directory"

    private val DATABASE_ENCRYPTED_SECRET = "pref_database_encrypted_secret"
    private val DATABASE_UNENCRYPTED_SECRET = "pref_database_unencrypted_secret"
    private val ATTACHMENT_ENCRYPTED_SECRET = "pref_attachment_encrypted_secret"
    private val ATTACHMENT_UNENCRYPTED_SECRET = "pref_attachment_unencrypted_secret"
    private val NEEDS_SQLCIPHER_MIGRATION = "pref_needs_sql_cipher_migration"

    val CALL_NOTIFICATIONS_PREF = "pref_call_notifications"
    val CALL_RINGTONE_PREF = "pref_call_ringtone"
    val CALL_VIBRATE_PREF = "pref_call_vibrate"

    private val NEXT_PRE_KEY_ID = "pref_next_pre_key_id"
    private val ACTIVE_SIGNED_PRE_KEY_ID = "pref_active_signed_pre_key_id"
    private val NEXT_SIGNED_PRE_KEY_ID = "pref_next_signed_pre_key_id"

    val BACKUP_ENABLED = "pref_backup_enabled"
    private val BACKUP_PASSPHRASE = "pref_backup_passphrase"
    private val BACKUP_TIME = "pref_backup_next_time"
    val BACKUP_NOW = "pref_backup_create"

    val SCREEN_LOCK = "pref_android_screen_lock"
    val SCREEN_LOCK_TIMEOUT = "pref_android_screen_lock_timeout"

    val REGISTRATION_LOCK_PREF = "pref_registration_lock"
    private val REGISTRATION_LOCK_PIN_PREF = "pref_registration_lock_pin"
    private val REGISTRATION_LOCK_LAST_REMINDER_TIME = "pref_registration_lock_last_reminder_time"
    private val REGISTRATION_LOCK_NEXT_REMINDER_INTERVAL = "pref_registration_lock_next_reminder_interval"

    private val SERVICE_OUTAGE = "pref_service_outage"
    private val LAST_OUTAGE_CHECK_TIME = "pref_last_outage_check_time"

    private val LAST_FULL_CONTACT_SYNC_TIME = "pref_last_full_contact_sync_time"
    private val NEEDS_FULL_CONTACT_SYNC = "pref_needs_full_contact_sync"

    private val LOG_ENCRYPTED_SECRET = "pref_log_encrypted_secret"
    private val LOG_UNENCRYPTED_SECRET = "pref_log_unencrypted_secret"

    private val NOTIFICATION_CHANNEL_VERSION = "pref_notification_channel_version"
    private val NOTIFICATION_MESSAGES_CHANNEL_VERSION = "pref_notification_messages_channel_version"

    fun isScreenLockEnabled(context: Context): Boolean {
        return getBooleanPreference(context, SCREEN_LOCK, false)
    }

    fun setScreenLockEnabled(context: Context, value: Boolean) {
        setBooleanPreference(context, SCREEN_LOCK, value)
    }

    fun getScreenLockTimeout(context: Context): Long {
        return getLongPreference(context, SCREEN_LOCK_TIMEOUT, 0)
    }

    fun setScreenLockTimeout(context: Context, value: Long) {
        setLongPreference(context, SCREEN_LOCK_TIMEOUT, value)
    }

    fun isRegistrationtLockEnabled(context: Context): Boolean {
        return getBooleanPreference(context, REGISTRATION_LOCK_PREF, false)
    }

    fun setRegistrationtLockEnabled(context: Context, value: Boolean) {
        setBooleanPreference(context, REGISTRATION_LOCK_PREF, value)
    }

    fun getRegistrationLockPin(context: Context): String? {
        return getStringPreference(context, REGISTRATION_LOCK_PIN_PREF, null)
    }

    fun setRegistrationLockPin(context: Context, pin: String) {
        setStringPreference(context, REGISTRATION_LOCK_PIN_PREF, pin)
    }

    fun getRegistrationLockLastReminderTime(context: Context): Long {
        return getLongPreference(context, REGISTRATION_LOCK_LAST_REMINDER_TIME, 0)
    }

    fun setRegistrationLockLastReminderTime(context: Context, time: Long) {
        setLongPreference(context, REGISTRATION_LOCK_LAST_REMINDER_TIME, time)
    }

//    fun getRegistrationLockNextReminderInterval(context: Context): Long {
//        return getLongPreference(context, REGISTRATION_LOCK_NEXT_REMINDER_INTERVAL, RegistrationLockReminders.INITIAL_INTERVAL)
//    }

    fun setRegistrationLockNextReminderInterval(context: Context, value: Long) {
        setLongPreference(context, REGISTRATION_LOCK_NEXT_REMINDER_INTERVAL, value)
    }

    fun setBackupPassphrase(context: Context, passphrase: String?) {
        setStringPreference(context, BACKUP_PASSPHRASE, passphrase)
    }

    fun getBackupPassphrase(context: Context): String? {
        return getStringPreference(context, BACKUP_PASSPHRASE, null)
    }

    fun setBackupEnabled(context: Context, value: Boolean) {
        setBooleanPreference(context, BACKUP_ENABLED, value)
    }

    fun isBackupEnabled(context: Context): Boolean {
        return getBooleanPreference(context, BACKUP_ENABLED, false)
    }

    fun setNextBackupTime(context: Context, time: Long) {
        setLongPreference(context, BACKUP_TIME, time)
    }

    fun getNextBackupTime(context: Context): Long {
        return getLongPreference(context, BACKUP_TIME, -1)
    }

//    fun getNextPreKeyId(context: Context): Int {
//        return getIntegerPreference(context, NEXT_PRE_KEY_ID, SecureRandom().nextInt(Medium.MAX_VALUE))
//    }

    fun setNextPreKeyId(context: Context, value: Int) {
        setIntegerPrefrence(context, NEXT_PRE_KEY_ID, value)
    }

//    fun getNextSignedPreKeyId(context: Context): Int {
//        return getIntegerPreference(context, NEXT_SIGNED_PRE_KEY_ID, SecureRandom().nextInt(Medium.MAX_VALUE))
//    }

    fun setNextSignedPreKeyId(context: Context, value: Int) {
        setIntegerPrefrence(context, NEXT_SIGNED_PRE_KEY_ID, value)
    }

    fun getActiveSignedPreKeyId(context: Context): Int {
        return getIntegerPreference(context, ACTIVE_SIGNED_PRE_KEY_ID, -1)
    }

    fun setActiveSignedPreKeyId(context: Context, value: Int) {
        setIntegerPrefrence(context, ACTIVE_SIGNED_PRE_KEY_ID, value)
    }

//    fun setNeedsSqlCipherMigration(context: Context, value: Boolean) {
//        setBooleanPreference(context, NEEDS_SQLCIPHER_MIGRATION, value)
//        EventBus.getDefault().post(SqlCipherMigrationRequirementProvider.SqlCipherNeedsMigrationEvent())
//    }

    fun getNeedsSqlCipherMigration(context: Context): Boolean {
        return getBooleanPreference(context, NEEDS_SQLCIPHER_MIGRATION, false)
    }

    fun setAttachmentEncryptedSecret(context: Context, secret: String) {
        setStringPreference(context, ATTACHMENT_ENCRYPTED_SECRET, secret)
    }

    fun setAttachmentUnencryptedSecret(context: Context, secret: String?) {
        setStringPreference(context, ATTACHMENT_UNENCRYPTED_SECRET, secret)
    }

    fun getAttachmentEncryptedSecret(context: Context): String? {
        return getStringPreference(context, ATTACHMENT_ENCRYPTED_SECRET, null)
    }

    fun getAttachmentUnencryptedSecret(context: Context): String? {
        return getStringPreference(context, ATTACHMENT_UNENCRYPTED_SECRET, null)
    }

    fun setDatabaseEncryptedSecret(context: Context, secret: String) {
        setStringPreference(context, DATABASE_ENCRYPTED_SECRET, secret)
    }

    fun setDatabaseUnencryptedSecret(context: Context, secret: String?) {
        setStringPreference(context, DATABASE_UNENCRYPTED_SECRET, secret)
    }

    fun getDatabaseUnencryptedSecret(context: Context): String? {
        return getStringPreference(context, DATABASE_UNENCRYPTED_SECRET, null)
    }

    fun getDatabaseEncryptedSecret(context: Context): String? {
        return getStringPreference(context, DATABASE_ENCRYPTED_SECRET, null)
    }

    fun setHasSuccessfullyRetrievedDirectory(context: Context, value: Boolean) {
        setBooleanPreference(context, SUCCESSFUL_DIRECTORY_PREF, value)
    }

    fun hasSuccessfullyRetrievedDirectory(context: Context): Boolean {
        return getBooleanPreference(context, SUCCESSFUL_DIRECTORY_PREF, false)
    }

    fun setUnauthorizedReceived(context: Context, value: Boolean) {
        setBooleanPreference(context, UNAUTHORIZED_RECEIVED, value)
    }

    fun isUnauthorizedRecieved(context: Context): Boolean {
        return getBooleanPreference(context, UNAUTHORIZED_RECEIVED, false)
    }

    fun isIncognitoKeyboardEnabled(context: Context): Boolean {
        return getBooleanPreference(context, INCOGNITO_KEYBORAD_PREF, false)
    }

    fun isReadReceiptsEnabled(context: Context): Boolean {
        return getBooleanPreference(context, READ_RECEIPTS_PREF, false)
    }

    fun setReadReceiptsEnabled(context: Context, enabled: Boolean) {
        setBooleanPreference(context, READ_RECEIPTS_PREF, enabled)
    }

    fun getProfileKey(context: Context): String? {
        return getStringPreference(context, PROFILE_KEY_PREF, null)
    }

    fun setProfileKey(context: Context, key: String) {
        setStringPreference(context, PROFILE_KEY_PREF, key)
    }

    fun setProfileName(context: Context, name: String) {
        setStringPreference(context, PROFILE_NAME_PREF, name)
    }

    fun getProfileName(context: Context): String? {
        return getStringPreference(context, PROFILE_NAME_PREF, null)
    }

    fun setProfileAvatarId(context: Context, id: Int) {
        setIntegerPrefrence(context, PROFILE_AVATAR_ID_PREF, id)
    }

    fun getProfileAvatarId(context: Context): Int {
        return getIntegerPreference(context, PROFILE_AVATAR_ID_PREF, 0)
    }

    fun getNotificationPriority(context: Context): Int {
        return Integer.valueOf(getStringPreference(context, NOTIFICATION_PRIORITY_PREF, NotificationCompat.PRIORITY_HIGH.toString()))
    }

    fun getMessageBodyTextSize(context: Context): Int {
        return Integer.valueOf(getStringPreference(context, MESSAGE_BODY_TEXT_SIZE_PREF, "16"))
    }

    fun isTurnOnly(context: Context): Boolean {
        return getBooleanPreference(context, ALWAYS_RELAY_CALLS_PREF, false)
    }

    fun isGcmDisabled(context: Context): Boolean {
        return getBooleanPreference(context, GCM_DISABLED_PREF, false)
    }

    fun setGcmDisabled(context: Context, disabled: Boolean) {
        setBooleanPreference(context, GCM_DISABLED_PREF, disabled)
    }

    fun isWebrtcCallingEnabled(context: Context): Boolean {
        return getBooleanPreference(context, WEBRTC_CALLING_PREF, false)
    }

    fun setWebrtcCallingEnabled(context: Context, enabled: Boolean) {
        setBooleanPreference(context, WEBRTC_CALLING_PREF, enabled)
    }

    fun setDirectCaptureCameraId(context: Context, value: Int) {
        setIntegerPrefrence(context, DIRECT_CAPTURE_CAMERA_ID, value)
    }

    fun getDirectCaptureCameraId(context: Context): Int {
        return getIntegerPreference(context, DIRECT_CAPTURE_CAMERA_ID, Camera.CameraInfo.CAMERA_FACING_FRONT)
    }

    fun setMultiDevice(context: Context, value: Boolean) {
        setBooleanPreference(context, MULTI_DEVICE_PROVISIONED_PREF, value)
    }

    fun isMultiDevice(context: Context): Boolean {
        return getBooleanPreference(context, MULTI_DEVICE_PROVISIONED_PREF, false)
    }

    fun setSignedPreKeyFailureCount(context: Context, value: Int) {
        setIntegerPrefrence(context, SIGNED_PREKEY_FAILURE_COUNT_PREF, value)
    }

    fun getSignedPreKeyFailureCount(context: Context): Int {
        return getIntegerPreference(context, SIGNED_PREKEY_FAILURE_COUNT_PREF, 0)
    }

//    fun getNotificationPrivacy(context: Context): NotificationPrivacyPreference {
//        return NotificationPrivacyPreference(getStringPreference(context, NOTIFICATION_PRIVACY_PREF, "all"))
//    }

    fun isNewContactsNotificationEnabled(context: Context): Boolean {
        return getBooleanPreference(context, NEW_CONTACTS_NOTIFICATIONS, true)
    }

    fun getRatingLaterTimestamp(context: Context): Long {
        return getLongPreference(context, RATING_LATER_PREF, 0)
    }

    fun setRatingLaterTimestamp(context: Context, timestamp: Long) {
        setLongPreference(context, RATING_LATER_PREF, timestamp)
    }

    fun isRatingEnabled(context: Context): Boolean {
        return getBooleanPreference(context, RATING_ENABLED_PREF, true)
    }

    fun setRatingEnabled(context: Context, enabled: Boolean) {
        setBooleanPreference(context, RATING_ENABLED_PREF, enabled)
    }

    fun isWebsocketRegistered(context: Context): Boolean {
        return getBooleanPreference(context, WEBSOCKET_REGISTERED_PREF, false)
    }

    fun setWebsocketRegistered(context: Context, registered: Boolean) {
        setBooleanPreference(context, WEBSOCKET_REGISTERED_PREF, registered)
    }

    fun isWifiSmsEnabled(context: Context): Boolean {
        return getBooleanPreference(context, WIFI_SMS_PREF, false)
    }

    fun getRepeatAlertsCount(context: Context): Int {
        try {
            return Integer.parseInt(getStringPreference(context, REPEAT_ALERTS_PREF, "0"))
        } catch (e: NumberFormatException) {
            Log.w(TAG, e)
            return 0
        }

    }

    fun setRepeatAlertsCount(context: Context, count: Int) {
        setStringPreference(context, REPEAT_ALERTS_PREF, count.toString())
    }

    fun isSignedPreKeyRegistered(context: Context): Boolean {
        return getBooleanPreference(context, SIGNED_PREKEY_REGISTERED_PREF, false)
    }

    fun setSignedPreKeyRegistered(context: Context, value: Boolean) {
        setBooleanPreference(context, SIGNED_PREKEY_REGISTERED_PREF, value)
    }

    fun setGcmRegistrationId(context: Context, registrationId: String) {
        setStringPreference(context, GCM_REGISTRATION_ID_PREF, registrationId)
        setIntegerPrefrence(context, GCM_REGISTRATION_ID_VERSION_PREF, VersionUtil.getAppVersionCode(context))
    }

    fun getGcmRegistrationId(context: Context): String? {
        val storedRegistrationIdVersion = getIntegerPreference(context, GCM_REGISTRATION_ID_VERSION_PREF, 0)

        return if (storedRegistrationIdVersion !=VersionUtil.getAppVersionCode(context)) {
            null
        } else {
            getStringPreference(context, GCM_REGISTRATION_ID_PREF, null)
        }
    }

    fun getGcmRegistrationIdLastSetTime(context: Context): Long {
        return getLongPreference(context, GCM_REGISTRATION_ID_TIME_PREF, 0)
    }

    fun setGcmRegistrationIdLastSetTime(context: Context, timestamp: Long) {
        setLongPreference(context, GCM_REGISTRATION_ID_TIME_PREF, timestamp)
    }

//    fun isSmsEnabled(context: Context): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Util.isDefaultSmsProvider(context)
//        } else {
//            isInterceptAllSmsEnabled(context)
//        }
//    }

    fun getLocalRegistrationId(context: Context): Int {
        return getIntegerPreference(context, LOCAL_REGISTRATION_ID_PREF, 0)
    }

    fun setLocalRegistrationId(context: Context, registrationId: Int) {
        setIntegerPrefrence(context, LOCAL_REGISTRATION_ID_PREF, registrationId)
    }

    fun isInThreadNotifications(context: Context): Boolean {
        return getBooleanPreference(context, IN_THREAD_NOTIFICATION_PREF, true)
    }

    fun getSignedPreKeyRotationTime(context: Context): Long {
        return getLongPreference(context, SIGNED_PREKEY_ROTATION_TIME_PREF, 0L)
    }

    fun setSignedPreKeyRotationTime(context: Context, value: Long) {
        setLongPreference(context, SIGNED_PREKEY_ROTATION_TIME_PREF, value)
    }

    fun getDirectoryRefreshTime(context: Context): Long {
        return getLongPreference(context, DIRECTORY_FRESH_TIME_PREF, 0L)
    }

    fun setDirectoryRefreshTime(context: Context, value: Long) {
        setLongPreference(context, DIRECTORY_FRESH_TIME_PREF, value)
    }

    fun getUpdateApkRefreshTime(context: Context): Long {
        return getLongPreference(context, UPDATE_APK_REFRESH_TIME_PREF, 0L)
    }

    fun setUpdateApkRefreshTime(context: Context, value: Long) {
        setLongPreference(context, UPDATE_APK_REFRESH_TIME_PREF, value)
    }

    fun setUpdateApkDownloadId(context: Context, value: Long) {
        setLongPreference(context, UPDATE_APK_DOWNLOAD_ID, value)
    }

    fun getUpdateApkDownloadId(context: Context): Long {
        return getLongPreference(context, UPDATE_APK_DOWNLOAD_ID, -1)
    }

    fun setUpdateApkDigest(context: Context, value: String) {
        setStringPreference(context, UPDATE_APK_DIGEST, value)
    }

    fun getUpdateApkDigest(context: Context): String? {
        return getStringPreference(context, UPDATE_APK_DIGEST, null)
    }

    fun getLocalNumber(context: Context): String? {
        return getStringPreference(context, LOCAL_NUMBER_PREF, null)
    }

    fun setLocalNumber(context: Context, localNumber: String) {
        setStringPreference(context, LOCAL_NUMBER_PREF, localNumber)
    }

    fun getPushServerPassword(context: Context): String? {
        return getStringPreference(context, GCM_PASSWORD_PREF, null)
    }

    fun setPushServerPassword(context: Context, password: String) {
        setStringPreference(context, GCM_PASSWORD_PREF, password)
    }

    fun setSignalingKey(context: Context, signalingKey: String) {
        setStringPreference(context, SIGNALING_KEY_PREF, signalingKey)
    }

    fun getSignalingKey(context: Context): String? {
        return getStringPreference(context, SIGNALING_KEY_PREF, null)
    }

    fun isEnterImeKeyEnabled(context: Context): Boolean {
        return getBooleanPreference(context, ENTER_PRESENT_PREF, false)
    }

    fun isEnterSendsEnabled(context: Context): Boolean {
        return getBooleanPreference(context, ENTER_SENDS_PREF, false)
    }

    fun isPasswordDisabled(context: Context): Boolean {
        return getBooleanPreference(context, DISABLE_PASSPHRASE_PREF, false)
    }

    fun setPasswordDisabled(context: Context, disabled: Boolean) {
        setBooleanPreference(context, DISABLE_PASSPHRASE_PREF, disabled)
    }

    fun getUseCustomMmsc(context: Context): Boolean {
        val legacy = TextSecurePreferences.isLegacyUseLocalApnsEnabled(context)
        return getBooleanPreference(context, MMSC_CUSTOM_HOST_PREF, legacy)
    }

    fun setUseCustomMmsc(context: Context, value: Boolean) {
        setBooleanPreference(context, MMSC_CUSTOM_HOST_PREF, value)
    }

    fun getMmscUrl(context: Context): String? {
        return getStringPreference(context, MMSC_HOST_PREF, "")
    }

    fun setMmscUrl(context: Context, mmsc: String) {
        setStringPreference(context, MMSC_HOST_PREF, mmsc)
    }

    fun getUseCustomMmscProxy(context: Context): Boolean {
        val legacy = TextSecurePreferences.isLegacyUseLocalApnsEnabled(context)
        return getBooleanPreference(context, MMSC_CUSTOM_PROXY_PREF, legacy)
    }

    fun setUseCustomMmscProxy(context: Context, value: Boolean) {
        setBooleanPreference(context, MMSC_CUSTOM_PROXY_PREF, value)
    }

    fun getMmscProxy(context: Context): String? {
        return getStringPreference(context, MMSC_PROXY_HOST_PREF, "")
    }

    fun setMmscProxy(context: Context, value: String) {
        setStringPreference(context, MMSC_PROXY_HOST_PREF, value)
    }

    fun getUseCustomMmscProxyPort(context: Context): Boolean {
        val legacy = TextSecurePreferences.isLegacyUseLocalApnsEnabled(context)
        return getBooleanPreference(context, MMSC_CUSTOM_PROXY_PORT_PREF, legacy)
    }

    fun setUseCustomMmscProxyPort(context: Context, value: Boolean) {
        setBooleanPreference(context, MMSC_CUSTOM_PROXY_PORT_PREF, value)
    }

    fun getMmscProxyPort(context: Context): String? {
        return getStringPreference(context, MMSC_PROXY_PORT_PREF, "")
    }

    fun setMmscProxyPort(context: Context, value: String) {
        setStringPreference(context, MMSC_PROXY_PORT_PREF, value)
    }

    fun getUseCustomMmscUsername(context: Context): Boolean {
        val legacy = TextSecurePreferences.isLegacyUseLocalApnsEnabled(context)
        return getBooleanPreference(context, MMSC_CUSTOM_USERNAME_PREF, legacy)
    }

    fun setUseCustomMmscUsername(context: Context, value: Boolean) {
        setBooleanPreference(context, MMSC_CUSTOM_USERNAME_PREF, value)
    }

    fun getMmscUsername(context: Context): String? {
        return getStringPreference(context, MMSC_USERNAME_PREF, "")
    }

    fun setMmscUsername(context: Context, value: String) {
        setStringPreference(context, MMSC_USERNAME_PREF, value)
    }

    fun getUseCustomMmscPassword(context: Context): Boolean {
        val legacy = TextSecurePreferences.isLegacyUseLocalApnsEnabled(context)
        return getBooleanPreference(context, MMSC_CUSTOM_PASSWORD_PREF, legacy)
    }

    fun setUseCustomMmscPassword(context: Context, value: Boolean) {
        setBooleanPreference(context, MMSC_CUSTOM_PASSWORD_PREF, value)
    }

    fun getMmscPassword(context: Context): String? {
        return getStringPreference(context, MMSC_PASSWORD_PREF, "")
    }

    fun setMmscPassword(context: Context, value: String) {
        setStringPreference(context, MMSC_PASSWORD_PREF, value)
    }

    fun getMmsUserAgent(context: Context, defaultUserAgent: String): String? {
        val useCustom = getBooleanPreference(context, MMS_CUSTOM_USER_AGENT, false)

        return if (useCustom)
            getStringPreference(context, MMS_USER_AGENT, defaultUserAgent)
        else
            defaultUserAgent
    }

    fun getIdentityContactUri(context: Context): String? {
        return getStringPreference(context, IDENTITY_PREF, null)
    }

    fun setIdentityContactUri(context: Context, identityUri: String) {
        setStringPreference(context, IDENTITY_PREF, identityUri)
    }

    fun setScreenSecurityEnabled(context: Context, value: Boolean) {
        setBooleanPreference(context, SCREEN_SECURITY_PREF, value)
    }

    fun isScreenSecurityEnabled(context: Context): Boolean {
        return getBooleanPreference(context, SCREEN_SECURITY_PREF, false)
    }

    fun isLegacyUseLocalApnsEnabled(context: Context): Boolean {
        return getBooleanPreference(context, ENABLE_MANUAL_MMS_PREF, false)
    }

    fun getLastVersionCode(context: Context): Int {
        return getIntegerPreference(context, LAST_VERSION_CODE_PREF, 0)
    }

    @Throws(IOException::class)
    fun setLastVersionCode(context: Context, versionCode: Int) {
        if (!setIntegerPrefrenceBlocking(context, LAST_VERSION_CODE_PREF, versionCode)) {
            throw IOException("couldn't write version code to sharedpreferences")
        }
    }

    fun getLastExperienceVersionCode(context: Context): Int {
        return getIntegerPreference(context, LAST_EXPERIENCE_VERSION_PREF, 0)
    }

    fun setLastExperienceVersionCode(context: Context, versionCode: Int) {
        setIntegerPrefrence(context, LAST_EXPERIENCE_VERSION_PREF, versionCode)
    }

    fun getExperienceDismissedVersionCode(context: Context): Int {
        return getIntegerPreference(context, EXPERIENCE_DISMISSED_PREF, 0)
    }

    fun setExperienceDismissedVersionCode(context: Context, versionCode: Int) {
        setIntegerPrefrence(context, EXPERIENCE_DISMISSED_PREF, versionCode)
    }

    fun getTheme(context: Context): String? {
        return getStringPreference(context, THEME_PREF, "light")
    }

    fun isVerifying(context: Context): Boolean {
        return getBooleanPreference(context, VERIFYING_STATE_PREF, false)
    }

    fun setVerifying(context: Context, verifying: Boolean) {
        setBooleanPreference(context, VERIFYING_STATE_PREF, verifying)
    }

    fun isPushRegistered(context: Context): Boolean {
        return getBooleanPreference(context, REGISTERED_GCM_PREF, false)
    }

    fun setPushRegistered(context: Context, registered: Boolean) {
        Log.i(TAG, "Setting push registered: $registered")
        setBooleanPreference(context, REGISTERED_GCM_PREF, registered)
    }

    fun isShowInviteReminders(context: Context): Boolean {
        return getBooleanPreference(context, SHOW_INVITE_REMINDER_PREF, true)
    }

    fun isPassphraseTimeoutEnabled(context: Context): Boolean {
        return getBooleanPreference(context, PASSPHRASE_TIMEOUT_PREF, false)
    }

    fun getPassphraseTimeoutInterval(context: Context): Int {
        return getIntegerPreference(context, PASSPHRASE_TIMEOUT_INTERVAL_PREF, 5 * 60)
    }

    fun setPassphraseTimeoutInterval(context: Context, interval: Int) {
        setIntegerPrefrence(context, PASSPHRASE_TIMEOUT_INTERVAL_PREF, interval)
    }

    fun getLanguage(context: Context): String? {
        return getStringPreference(context, LANGUAGE_PREF, "zz")
    }

    fun setLanguage(context: Context, language: String) {
        setStringPreference(context, LANGUAGE_PREF, language)
    }

    fun isSmsDeliveryReportsEnabled(context: Context): Boolean {
        return getBooleanPreference(context, SMS_DELIVERY_REPORT_PREF, false)
    }

    fun hasPromptedPushRegistration(context: Context): Boolean {
        return getBooleanPreference(context, PROMPTED_PUSH_REGISTRATION_PREF, false)
    }

    fun setPromptedPushRegistration(context: Context, value: Boolean) {
        setBooleanPreference(context, PROMPTED_PUSH_REGISTRATION_PREF, value)
    }

    fun hasPromptedDefaultSmsProvider(context: Context): Boolean {
        return getBooleanPreference(context, PROMPTED_DEFAULT_SMS_PREF, false)
    }

    fun setPromptedDefaultSmsProvider(context: Context, value: Boolean) {
        setBooleanPreference(context, PROMPTED_DEFAULT_SMS_PREF, value)
    }

    fun setPromptedOptimizeDoze(context: Context, value: Boolean) {
        setBooleanPreference(context, PROMPTED_OPTIMIZE_DOZE_PREF, value)
    }

    fun hasPromptedOptimizeDoze(context: Context): Boolean {
        return getBooleanPreference(context, PROMPTED_OPTIMIZE_DOZE_PREF, false)
    }

    fun hasPromptedShare(context: Context): Boolean {
        return getBooleanPreference(context, PROMPTED_SHARE_PREF, false)
    }

    fun setPromptedShare(context: Context, value: Boolean) {
        setBooleanPreference(context, PROMPTED_SHARE_PREF, value)
    }

    fun isInterceptAllMmsEnabled(context: Context): Boolean {
        return getBooleanPreference(context, ALL_MMS_PREF, true)
    }

    fun isInterceptAllSmsEnabled(context: Context): Boolean {
        return getBooleanPreference(context, ALL_SMS_PREF, true)
    }

    fun isNotificationsEnabled(context: Context): Boolean {
        return getBooleanPreference(context, NOTIFICATION_PREF, true)
    }

    fun isCallNotificationsEnabled(context: Context): Boolean {
        return getBooleanPreference(context, CALL_NOTIFICATIONS_PREF, true)
    }

    fun getNotificationRingtone(context: Context): Uri {
        var result = getStringPreference(context, RINGTONE_PREF, Settings.System.DEFAULT_NOTIFICATION_URI.toString())

        if (result != null && result.startsWith("file:")) {
            result = Settings.System.DEFAULT_NOTIFICATION_URI.toString()
        }

        return Uri.parse(result)
    }

    fun getCallNotificationRingtone(context: Context): Uri {
        var result = getStringPreference(context, CALL_RINGTONE_PREF, Settings.System.DEFAULT_RINGTONE_URI.toString())

        if (result != null && result.startsWith("file:")) {
            result = Settings.System.DEFAULT_RINGTONE_URI.toString()
        }

        return Uri.parse(result)
    }

    fun removeNotificationRingtone(context: Context) {
        removePreference(context, RINGTONE_PREF)
    }

    fun removeCallNotificationRingtone(context: Context) {
        removePreference(context, CALL_RINGTONE_PREF)
    }

    fun setNotificationRingtone(context: Context, ringtone: String) {
        setStringPreference(context, RINGTONE_PREF, ringtone)
    }

    fun setCallNotificationRingtone(context: Context, ringtone: String) {
        setStringPreference(context, CALL_RINGTONE_PREF, ringtone)
    }

    fun setNotificationVibrateEnabled(context: Context, enabled: Boolean) {
        setBooleanPreference(context, VIBRATE_PREF, enabled)
    }

    fun isNotificationVibrateEnabled(context: Context): Boolean {
        return getBooleanPreference(context, VIBRATE_PREF, true)
    }

    fun isCallNotificationVibrateEnabled(context: Context): Boolean {
        var defaultValue = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            defaultValue = Settings.System.getInt(context.contentResolver, Settings.System.VIBRATE_WHEN_RINGING, 1) == 1
        }

        return getBooleanPreference(context, CALL_VIBRATE_PREF, defaultValue)
    }

    fun getNotificationLedColor(context: Context): String? {
        return getStringPreference(context, LED_COLOR_PREF, "blue")
    }

    fun getNotificationLedPattern(context: Context): String? {
        return getStringPreference(context, LED_BLINK_PREF, "500,2000")
    }

    fun getNotificationLedPatternCustom(context: Context): String? {
        return getStringPreference(context, LED_BLINK_PREF_CUSTOM, "500,2000")
    }

    fun setNotificationLedPatternCustom(context: Context, pattern: String) {
        setStringPreference(context, LED_BLINK_PREF_CUSTOM, pattern)
    }

    fun isThreadLengthTrimmingEnabled(context: Context): Boolean {
        return getBooleanPreference(context, THREAD_TRIM_ENABLED, false)
    }

    fun getThreadTrimLength(context: Context): Int {
        return Integer.parseInt(getStringPreference(context, THREAD_TRIM_LENGTH, "500"))
    }

    fun isSystemEmojiPreferred(context: Context): Boolean {
        return getBooleanPreference(context, SYSTEM_EMOJI_PREF, false)
    }

//    fun getMobileMediaDownloadAllowed(context: Context): Set<String> {
//        return getMediaDownloadAllowed(context, MEDIA_DOWNLOAD_MOBILE_PREF, R.array.pref_media_download_mobile_data_default)
//    }
//
//    fun getWifiMediaDownloadAllowed(context: Context): Set<String> {
//        return getMediaDownloadAllowed(context, MEDIA_DOWNLOAD_WIFI_PREF, R.array.pref_media_download_wifi_default)
//    }
//
//    fun getRoamingMediaDownloadAllowed(context: Context): Set<String> {
//        return getMediaDownloadAllowed(context, MEDIA_DOWNLOAD_ROAMING_PREF, R.array.pref_media_download_roaming_default)
//    }

//    private fun getMediaDownloadAllowed(context: Context, key: String, @ArrayRes defaultValuesRes: Int): Set<String> {
//        return getStringSetPreference(context,
//                key,
//                HashSet(Arrays.asList(*context.resources.getStringArray(defaultValuesRes))))
//    }

    fun setLastOutageCheckTime(context: Context, timestamp: Long) {
        setLongPreference(context, LAST_OUTAGE_CHECK_TIME, timestamp)
    }

    fun getLastOutageCheckTime(context: Context): Long {
        return getLongPreference(context, LAST_OUTAGE_CHECK_TIME, 0)
    }

    fun setServiceOutage(context: Context, isOutage: Boolean) {
        setBooleanPreference(context, SERVICE_OUTAGE, isOutage)
    }

    fun getServiceOutage(context: Context): Boolean {
        return getBooleanPreference(context, SERVICE_OUTAGE, false)
    }

    fun getLastFullContactSyncTime(context: Context): Long {
        return getLongPreference(context, LAST_FULL_CONTACT_SYNC_TIME, 0)
    }

    fun setLastFullContactSyncTime(context: Context, timestamp: Long) {
        setLongPreference(context, LAST_FULL_CONTACT_SYNC_TIME, timestamp)
    }

    fun needsFullContactSync(context: Context): Boolean {
        return getBooleanPreference(context, NEEDS_FULL_CONTACT_SYNC, false)
    }

    fun setNeedsFullContactSync(context: Context, needsSync: Boolean) {
        setBooleanPreference(context, NEEDS_FULL_CONTACT_SYNC, needsSync)
    }

    fun setLogEncryptedSecret(context: Context, base64Secret: String) {
        setStringPreference(context, LOG_ENCRYPTED_SECRET, base64Secret)
    }

    fun getLogEncryptedSecret(context: Context): String? {
        return getStringPreference(context, LOG_ENCRYPTED_SECRET, null)
    }

    fun setLogUnencryptedSecret(context: Context, base64Secret: String) {
        setStringPreference(context, LOG_UNENCRYPTED_SECRET, base64Secret)
    }

    fun getLogUnencryptedSecret(context: Context): String? {
        return getStringPreference(context, LOG_UNENCRYPTED_SECRET, null)
    }

    fun getNotificationChannelVersion(context: Context): Int {
        return getIntegerPreference(context, NOTIFICATION_CHANNEL_VERSION, 1)
    }

    fun setNotificationChannelVersion(context: Context, version: Int) {
        setIntegerPrefrence(context, NOTIFICATION_CHANNEL_VERSION, version)
    }

    fun getNotificationMessagesChannelVersion(context: Context): Int {
        return getIntegerPreference(context, NOTIFICATION_MESSAGES_CHANNEL_VERSION, 1)
    }

    fun setNotificationMessagesChannelVersion(context: Context, version: Int) {
        setIntegerPrefrence(context, NOTIFICATION_MESSAGES_CHANNEL_VERSION, version)
    }

    fun setBooleanPreference(context: Context, key: String, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply()
    }

    fun getBooleanPreference(context: Context, key: String, defaultValue: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue)
    }

    fun setStringPreference(context: Context, key: String, value: String?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply()
    }

    fun getStringPreference(context: Context, key: String, defaultValue: String?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue)
    }

    private fun getIntegerPreference(context: Context, key: String, defaultValue: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue)
    }

    private fun setIntegerPrefrence(context: Context, key: String, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply()
    }

    private fun setIntegerPrefrenceBlocking(context: Context, key: String, value: Int): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).commit()
    }

    private fun getLongPreference(context: Context, key: String, defaultValue: Long): Long {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defaultValue)
    }

    private fun setLongPreference(context: Context, key: String, value: Long) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, value).apply()
    }

    private fun removePreference(context: Context, key: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply()
    }

    private fun getStringSetPreference(context: Context, key: String, defaultValues: Set<String>): Set<String>? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return if (prefs.contains(key)) {
            prefs.getStringSet(key, emptySet())
        } else {
            defaultValues
        }
    }
}