set -e
../gradlew assembleDebug
adb install -r app/build/outputs/apk/dev/debug/app-dev-debug.apk
adb shell am start -n "com.dokiwa.dokidoki/com.dokiwa.dokidoki.admin.activity.AdminActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
