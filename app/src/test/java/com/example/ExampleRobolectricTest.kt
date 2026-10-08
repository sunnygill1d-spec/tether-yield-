package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.InvestmentPlansData
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TetherYield", appName)
    }

    @Test
    fun `verify fifteen investment plans with 0_5 percent daily return`() {
        val plans = InvestmentPlansData.ALL_PLANS
        assertEquals(15, plans.size)
        assertEquals(5.0, plans.first().amountUsdt, 0.001)
        assertEquals(1000.0, plans.last().amountUsdt, 0.001)

        // Verify all plans have 0.5% daily rate
        plans.forEach { plan ->
            assertEquals(0.5, plan.dailyRatePercent, 0.001)
            assertEquals(plan.amountUsdt * 0.005, plan.dailyYieldUsdt, 0.0001)
        }
    }
}
