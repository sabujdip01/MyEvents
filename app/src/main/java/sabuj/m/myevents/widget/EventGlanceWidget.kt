package sabuj.m.myevents.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import sabuj.m.myevents.core.theme.AppColor

/** Glance App Widget showing the next upcoming event and budget progress. */
class EventGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(AppColor.primary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "My Events",
                style = TextStyle(
                    color = androidx.glance.color.ColorProvider(day = AppColor.onPrimary, night = AppColor.onPrimary),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(GlanceModifier.height(8.dp))
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Upcoming Event: ",
                    style = TextStyle(
                        color = androidx.glance.color.ColorProvider(day = AppColor.onPrimary.copy(alpha = 0.8f), night = AppColor.onPrimary.copy(alpha = 0.8f)),
                        fontSize = 14.sp,
                    ),
                )
                Text(
                    text = "Active Events Planned",
                    style = TextStyle(
                        color = androidx.glance.color.ColorProvider(day = AppColor.onPrimary, night = AppColor.onPrimary),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }
}

/** Receiver for [EventGlanceWidget]. */
class EventGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EventGlanceWidget()
}
