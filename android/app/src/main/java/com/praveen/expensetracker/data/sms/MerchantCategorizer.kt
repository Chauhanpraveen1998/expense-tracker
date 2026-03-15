package com.praveen.expensetracker.data.sms

import com.praveen.expensetracker.domain.model.Category

object MerchantCategorizer {
    
    private val categoryKeywords = mapOf(
        Category.FOOD_DINING to listOf(
            "swiggy", "zomato", "dominos", "pizza", "mcdonalds", "kfc", "burger",
            "starbucks", "cafe", "restaurant", "food", "eat", "dine", "kitchen",
            "biryani", "chinese", "italian", "subway", "dunkin"
        ),
        Category.GROCERIES to listOf(
            "bigbasket", "grofers", "blinkit", "zepto", "dmart", "reliance",
            "more", "spencer", "grocery", "supermarket", "mart", "vegetables"
        ),
        Category.TRANSPORT to listOf(
            "uber", "ola", "rapido", "metro", "irctc", "redbus", "makemytrip",
            "goibibo", "cab", "taxi", "auto", "railway", "bus", "travel"
        ),
        Category.FUEL to listOf(
            "petrol", "diesel", "fuel", "indian oil", "hp", "bharat petroleum",
            "iocl", "bpcl", "hpcl", "shell", "essar"
        ),
        Category.SHOPPING to listOf(
            "amazon", "flipkart", "myntra", "ajio", "meesho", "nykaa",
            "decathlon", "croma", "reliance digital", "shopping", "mall"
        ),
        Category.ENTERTAINMENT to listOf(
            "netflix", "prime video", "hotstar", "spotify", "youtube", "pvr",
            "inox", "cinema", "movie", "bookmyshow", "game", "playstation"
        ),
        Category.BILLS_UTILITIES to listOf(
            "electricity", "water", "gas", "internet", "broadband", "mobile",
            "recharge", "airtel", "jio", "vodafone", "vi", "bsnl", "bill",
            "rent", "maintenance", "society"
        ),
        Category.HEALTH to listOf(
            "pharmacy", "medical", "hospital", "doctor", "clinic", "apollo",
            "medplus", "netmeds", "pharmeasy", "1mg", "health", "medicine"
        ),
        Category.EDUCATION to listOf(
            "school", "college", "university", "course", "udemy", "coursera",
            "education", "tuition", "books", "stationery"
        ),
        Category.INVESTMENTS to listOf(
            "zerodha", "groww", "upstox", "mutual fund", "sip", "investment",
            "stock", "share", "demat", "trading"
        ),
        Category.SALARY to listOf(
            "salary", "payroll", "stipend", "wages"
        ),
        Category.FREELANCE to listOf(
            "freelance", "consulting", "project", "client payment"
        )
    )
    
    fun categorize(merchantName: String?): Category {
        if (merchantName.isNullOrBlank()) return Category.OTHER_EXPENSE
        
        val lowerName = merchantName.lowercase()
        
        for ((category, keywords) in categoryKeywords) {
            if (keywords.any { keyword -> lowerName.contains(keyword) }) {
                return category
            }
        }
        
        return Category.OTHER_EXPENSE
    }
}
