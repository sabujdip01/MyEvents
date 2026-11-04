package sabuj.m.myevents.core.icons

import androidx.annotation.DrawableRes
import sabuj.m.myevents.R

enum class EventCategoryIcon(
    @DrawableRes val drawableRes: Int,
    val title: String
) {
    BirthdayPersonal(R.drawable.ic_category_birthday, "Birthday & Personal"),
    EngagementPreWedding(R.drawable.ic_category_engagement, "Engagement & Pre-Wedding"),
    MarriageWedding(R.drawable.ic_category_marriage, "Marriage & Wedding"),
    ReceptionPostWedding(R.drawable.ic_category_reception, "Reception & Post-Wedding"),
    PujaReligious(R.drawable.ic_category_puja, "Puja & Religious"),
    BirthChildhood(R.drawable.ic_category_childhood, "Birth & Childhood"),
    TraditionalFamilyRituals(R.drawable.ic_category_traditional, "Traditional & Family Rituals"),
    HoneymoonRomantic(R.drawable.ic_category_honeymoon, "Honeymoon & Romantic"),
    ReligiousCulturalFestivals(R.drawable.ic_category_festivals, "Religious & Cultural Festivals"),
    SocialCommunity(R.drawable.ic_category_community, "Social & Community")
}
