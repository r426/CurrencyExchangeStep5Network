# CurrencyExchangeStep4LiveDataEncapsulated
Further development of CurrencyExchange app. LiveData encapsulated.

Privacy matters. So, in MainViewModel, all MutableLiveData goes private and is returned by getters. This way,

    val euro = MutableLiveData<Currency>()

turns to

    private val _euro = MutableLiveData<Currency>()
    val euro: LiveData<Currency>
        get() = _euro

Naturally, ```_euro``` is used everywhere within MainViewModel instead of ```euro```.

Learned from [Android Kotlin Fundamentals 05.2: LiveData and LiveData observers, Task 6](https://codelabs.developers.google.com/codelabs/kotlin-android-training-live-data/index.html?index=..%2F..android-kotlin-fundamentals#5)
