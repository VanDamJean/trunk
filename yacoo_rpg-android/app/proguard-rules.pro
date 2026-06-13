# Yacoo RPG - ProGuard Rules

# Keep all game model classes (used by manual JSON serialization)
-keepclassmembers class com.yacoo.rpg.game.** {
    <fields>;
}

# Keep enum names for serialization
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
