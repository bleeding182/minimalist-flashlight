
-optimizationpasses 100

-repackageclasses ''
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}