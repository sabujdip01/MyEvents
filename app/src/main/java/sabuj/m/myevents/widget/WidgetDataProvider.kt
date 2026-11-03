package sabuj.m.myevents.widget

import android.content.Context
import sabuj.m.myevents.domain.model.Event

/**
 * Provides data for [EventWidget] from the local Room database.
 * In future phases this will inject repositories and expose a Flow
 * of widget state; currently it returns placeholder data.
 */
object WidgetDataProvider {

    /**
     * Return the next upcoming event, or null if there are none.
     * TODO: inject [EventRepository] and query the DB.
     */
    suspend fun nextEvent(context: Context): Event? = null
}

