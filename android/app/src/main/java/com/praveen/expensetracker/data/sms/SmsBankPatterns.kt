package com.praveen.expensetracker.data.sms

import com.praveen.expensetracker.domain.model.TransactionType

data class ParsedSms(
    val amount: Double,
    val type: TransactionType,
    val merchantName: String?,
    val accountLastFour: String?,
    val balance: Double?,
    val referenceNumber: String?,
    val rawMessage: String,
    val smsHash: String
)

object SmsBankPatterns {
    
    private val debitPatterns = listOf(
        Regex("""(?i)debited.*?Rs\.?\s*([\d,]+\.?\d*).*?(?:at|to|for)\s+([A-Za-z0-9\s]+?)(?:\s+on|\s+Ref|\.|$)"""),
        Regex("""(?i)INR\s*([\d,]+\.?\d*)\s+debited.*?(?:to|at)\s+([A-Za-z0-9\s]+?)(?:\s+on|\.|$)"""),
        Regex("""(?i)Rs\.?\s*([\d,]+\.?\d*)\s+(?:has been |)debited.*?(?:for|to)\s+([A-Za-z0-9\s]+)"""),
        Regex("""(?i)spent\s+Rs\.?\s*([\d,]+\.?\d*).*?(?:at|on)\s+([A-Za-z0-9\s]+)"""),
        Regex("""(?i)(?:sent|paid|debited)\s+Rs\.?\s*([\d,]+\.?\d*).*?(?:to|for)\s+([A-Za-z0-9\s@]+)"""),
        Regex("""(?i)(?:txn|transaction).*?Rs\.?\s*([\d,]+\.?\d*).*?(?:at|on)\s+([A-Za-z0-9\s]+)""")
    )
    
    private val creditPatterns = listOf(
        Regex("""(?i)credited.*?Rs\.?\s*([\d,]+\.?\d*).*?(?:from|by)\s+([A-Za-z0-9\s]+?)(?:\s+on|\.|$)"""),
        Regex("""(?i)received\s+Rs\.?\s*([\d,]+\.?\d*).*?(?:from)\s+([A-Za-z0-9\s@]+)"""),
        Regex("""(?i)refund.*?Rs\.?\s*([\d,]+\.?\d*)"""),
        Regex("""(?i)INR\s*([\d,]+\.?\d*)\s+credited""")
    )
    
    private val accountPattern = Regex("""(?i)(?:a/c|account|acct).*?[xX*]+(\d{4})""")
    private val balancePattern = Regex("""(?i)(?:bal|balance|avl\.?\s*bal).*?Rs\.?\s*([\d,]+\.?\d*)""")
    private val referencePattern = Regex("""(?i)(?:ref|reference|txn).*?(\d{10,})""")
    
    fun parse(smsBody: String, sender: String): ParsedSms? {
        val normalizedBody = smsBody.replace("\n", " ").trim()
        
        for (pattern in debitPatterns) {
            val match = pattern.find(normalizedBody)
            if (match != null) {
                val amount = parseAmount(match.groupValues[1])
                val merchant = match.groupValues.getOrNull(2)?.trim()?.take(50)
                
                if (amount != null && amount > 0) {
                    return ParsedSms(
                        amount = amount,
                        type = TransactionType.EXPENSE,
                        merchantName = cleanMerchantName(merchant),
                        accountLastFour = extractAccountNumber(normalizedBody),
                        balance = extractBalance(normalizedBody),
                        referenceNumber = extractReference(normalizedBody),
                        rawMessage = smsBody,
                        smsHash = generateHash(smsBody)
                    )
                }
            }
        }
        
        for (pattern in creditPatterns) {
            val match = pattern.find(normalizedBody)
            if (match != null) {
                val amount = parseAmount(match.groupValues[1])
                val source = match.groupValues.getOrNull(2)?.trim()?.take(50)
                
                if (amount != null && amount > 0) {
                    return ParsedSms(
                        amount = amount,
                        type = TransactionType.INCOME,
                        merchantName = cleanMerchantName(source) ?: "Credit",
                        accountLastFour = extractAccountNumber(normalizedBody),
                        balance = extractBalance(normalizedBody),
                        referenceNumber = extractReference(normalizedBody),
                        rawMessage = smsBody,
                        smsHash = generateHash(smsBody)
                    )
                }
            }
        }
        
        return null
    }
    
    private fun parseAmount(amountStr: String): Double? {
        return amountStr.replace(",", "").toDoubleOrNull()
    }
    
    private fun extractAccountNumber(body: String): String? {
        return accountPattern.find(body)?.groupValues?.get(1)
    }
    
    private fun extractBalance(body: String): Double? {
        val match = balancePattern.find(body)
        return match?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
    }
    
    private fun extractReference(body: String): String? {
        return referencePattern.find(body)?.groupValues?.get(1)
    }
    
    private fun cleanMerchantName(name: String?): String? {
        if (name.isNullOrBlank()) return null
        
        return name
            .replace(Regex("""(?i)\s*(pvt|ltd|llp|inc|corp)\.?\s*"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()
            .takeIf { it.length >= 2 }
    }
    
    private fun generateHash(smsBody: String): String {
        return smsBody.hashCode().toString()
    }
}
