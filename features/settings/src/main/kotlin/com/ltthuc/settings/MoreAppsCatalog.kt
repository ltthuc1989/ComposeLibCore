package com.ltthuc.settings

import androidx.annotation.DrawableRes

data class MoreApp(
    @DrawableRes val icon: Int,
    val name: String,
    val subtitle: String,
    val packageName: String,
)

object MoreAppsCatalog {
    val entries = listOf(
        MoreApp(R.drawable.icon_app_heatindex, "Heat Index Calculator - How to", "Calculate heat & humidity", "trithuc.heatindex.calc"),
        MoreApp(R.drawable.icon_app_airquality, "Air quality Index - PM2.5", "Track air quality near you", "com.trithuc.airquality"),
        MoreApp(R.drawable.icon_app_fluent_calc, "Fluent Stuttering Calculator", "Speech fluency tracker", "com.trithuc.fluencycalculator"),
        MoreApp(R.drawable.icon_app_mortgage_calc, "Mortgage Payoff Calculator", "Plan your mortgage payoff", "com.trithuc.mortgagecalc"),
        MoreApp(R.drawable.icon_app_investment_calc, "Investment Calculator", "Estimate investment returns", "trithuc.investment.calc"),
        MoreApp(R.drawable.icon_app_atmortizaiton_calc, "Amortizing Loan Calculator", "Loan amortization schedule", "trithuc.amortization.calc"),
        MoreApp(R.drawable.icon_app_cd_calculator, "CD Calculator", "Certificate of deposit math", "com.trithuc.cdcalculator"),
        MoreApp(R.drawable.icon_app_dewpoint, "Dew Point Humidity Calculator", "Compute dew point", "com.trithuc.dewpoint"),
        MoreApp(R.drawable.icon_app_windchill, "Wind chill & Humid Calculators", "Wind chill index", "trithuc.windchill.calc"),
        MoreApp(R.drawable.icon_app_positionsize, "Position Size Lots Pip Calc Fx", "FX position sizing", "com.trithuc.positionsize.app"),
    )
}

const val SUPPORT_EMAIL = "dotri84@gmail.com"
const val PRIVACY_URL = "https://composetemplate.dev/privacy"
