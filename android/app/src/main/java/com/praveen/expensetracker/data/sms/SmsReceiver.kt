package com.praveen.expensetracker.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.praveen.expensetracker.domain.usecase.ProcessSmsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var processSmsUseCase: ProcessSmsUseCase
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        
        for (sms in messages) {
            val sender = sms.displayOriginatingAddress ?: continue
            val body = sms.messageBody ?: continue
            
            if (isBankSender(sender)) {
                scope.launch {
                    processSmsUseCase(body, sender)
                }
            }
        }
    }
    
    private fun isBankSender(sender: String): Boolean {
        val bankPrefixes = listOf(
            "HDFC", "ICICI", "SBI", "AXIS", "KOTAK", "PNB", "BOB", "IDBI",
            "YES", "INDUS", "RBL", "FEDERAL", "CITI", "HSBC", "SCBANK"
        )
        
        val senderUpper = sender.uppercase()
        return bankPrefixes.any { senderUpper.contains(it) } ||
               sender.matches(Regex("""^[A-Z]{2}-[A-Z]+$""")) ||
               sender.matches(Regex("""^\d{6}$"""))
    }
}
