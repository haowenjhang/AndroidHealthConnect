package com.wen.healthconnect_lib

import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.unity3d.player.UnityPlayer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant

class MainActivity : UnityPlayerActivity() {

    var Tag:String  = "MyLog"

    // Android HealthConnect
    var healthConnectClient:HealthConnectClient? = null
    var requestPermissionActivityContract: ActivityResultContract<Set<Permission>, Set<Permission>>? = null
    var requestPermissions: ActivityResultLauncher<Set<Permission>>? = null
    val weightList = mutableListOf<String>()


    // 需要用到的授權要在 src/res/values 裡的 health_permissions xml 新增
    val PERMISSIONS =
        setOf(
           Permission.createReadPermission(WeightRecord::class),
           // Permission.createReadPermission(BodyTemperatureRecord::class)
        )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(Tag,"OnCreate!!!")


        // Android HealthConnect
        if (HealthConnectClient.isAvailable(UnityPlayer.currentActivity)) {
            // Health Connect is available and installed.
            healthConnectClient = HealthConnectClient.getOrCreate(UnityPlayer.currentActivity)

            requestPermissionActivityContract = (healthConnectClient as HealthConnectClient).permissionController.createRequestPermissionActivityContract()

            requestPermissions =
                registerForActivityResult(HealthDataRequestPermissions()) { granted ->
                    if (granted.containsAll(PERMISSIONS)) {

                        // Permissions successfully granted
                    } else {

                        // Lack of required permissions
                    }
                }
        }
        else {
                    //
        }
    }

    fun checkPermissionsAndRun(){
        lifecycleScope.launch {
            val granted =  (healthConnectClient as HealthConnectClient).permissionController.getGrantedPermissions(PERMISSIONS)
            if (granted.containsAll(PERMISSIONS)) {
                // Permissions already granted
            } else {
                (requestPermissions as ActivityResultLauncher<Set<Permission>>).launch(PERMISSIONS)
            }
        }
    }


    suspend fun readWeight() {
        val response =
            (healthConnectClient as HealthConnectClient).readRecords(
                ReadRecordsRequest(
                    recordType = WeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.after(Instant.now(Clock.systemDefaultZone()).minusSeconds(60 * 60 * 24)),
                    ascendingOrder = false
                )
            )
        for (WeightRecord in response.records) {

            weightList.add(WeightRecord.time.toString() + " : " + WeightRecord.weight);
            Log.d(Tag,WeightRecord.weight.toString())
        }

        receiveStr(weightList.joinToString())
    }


    fun getWeight(){
        lifecycleScope.launch {
            readWeight()
        }
    }

    fun receiveStr(str: String?) {

        if(weightList != null){
            UnityPlayer.UnitySendMessage("Canvas","GetDataInfo",str);
        }
    }
}