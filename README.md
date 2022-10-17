# AndroidHealthConnect

AndroidHealthConnect is a library for Unity

# Usage
1. Add UnityPlayerActivity.java
2. Add Unity classes.jar to /libs and as a Library
3. Add permission in res/values/health_permissions.xml
```xml
    <array name="health_permissions">
        <item>androidx.health.permission.Weight.READ</item>
    </array>
```

4. Add in AndroidManifest.xml
```xml
  <intent-filter>
      <action android:name="androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE"/>
   </intent-filter>
   <meta-data android:name="health_permissions"
       android:resource="@array/health_permissions" />
```

5. Add in build.gradle
```kotlin
 implementation 'androidx.health:health-connect-client:1.0.0-alpha03'
```


Finally build .aar for Unity 
