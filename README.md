[![Release](https://jitpack.io/v/PushDevonics/push-devonics-android.svg)](https://jitpack.io/#PushDevonics/push-devonics-android)

Add it to you settings.gradle in repositories:

    repositories {
            google()
            mavenCentral()
            maven { url 'https://jitpack.io' }
    }
and:

    dependencies {
        implementation platform('com.google.firebase:firebase-bom:28.3.1')
        implementation 'com.google.firebase:firebase-messaging-ktx'
        implementation 'com.github.PushDevonics:push-devonics-android:latest version'
    }

If you want single Activity:
    
Kotlin:

MainActivity:

    private lateinit var pushDevonics: PushDevonics
    
MainActivity in onCreate():

    pushDevonics = PushDevonics(this, "appId")
    lifecycle.addObserver(pushDevonics)
    
    // If you need internalId
    val internalId = pushDevonics.getInternalId()
    
    // If you want add tag type String
    pushDevonics.setTags("key", "value")
    
    // If you need deeplink
    val deepLink = pushDevonics.getDeeplink()
    
Java:

MainActivity:

    private PushDevonics pushDevonics;
    
MainActivity in onCreate():

    pushDevonics = new PushDevonics(this, "appId");
    getLifecycle().addObserver(pushDevonics);
        
    // If you need internalId
    String internalId = pushDevonics.getInternalId();
    
    // If you want add tag type String
    pushDevonics.setTags("key", "value");
    
    // If you need deeplink
    String deeplink = pushDevonics.getDeeplink();
        
If you want many Activities:

You need to create a class inherited from Application and initiate it like for an Activity

in onCreate():

    pushDevonics = new PushDevonics(this, "appId");
    registerActivityLifecycleCallbacks(pushDevonics);