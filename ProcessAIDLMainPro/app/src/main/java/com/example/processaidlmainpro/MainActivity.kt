package com.example.processaidlmainpro

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.example.processaidlmainpro.databinding.ActivityMainBinding
import com.example.processaidlservicepro.MyAIDLInterface
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var serviceConnection: ServiceConnection
    var progressCoroutineJob: Job? = null
    var myAIDLInterface:MyAIDLInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                //MyAIDLInterface 받음
                myAIDLInterface = MyAIDLInterface.Stub.asInterface(service)
                // 10번 메세지 전달
                myAIDLInterface?.start()
                // 노래 총시간을 가져온다.
                binding.messengerProgress.max = myAIDLInterface!!.maxDuration
                // 코루틴 통해서 프로그래스바 진행
                val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
                progressCoroutineJob = backgroundScope.launch {
                    while (binding.messengerProgress.progress < binding.messengerProgress.max){
                        delay(1000)
                        binding.messengerProgress.incrementProgressBy(1000)
                    }
                    myAIDLInterface?.stop()
                    unbindService(serviceConnection)
                    progressCoroutineJob?.cancel()
                    binding.messengerProgress.progress = 0
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                myAIDLInterface = null
            }
        }
        binding.messengerPlay.setOnClickListener {
            val intent = Intent("ACTION_SERVICE_AIDL")
            intent.setPackage("com.example.processaidlservicepro")
            bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)
        }
        binding.messengerStop.setOnClickListener {
            myAIDLInterface?.stop()
            progressCoroutineJob?.cancel()
//            unbindService(serviceConnection)
            binding.messengerProgress.progress = 0
        }
    }
}