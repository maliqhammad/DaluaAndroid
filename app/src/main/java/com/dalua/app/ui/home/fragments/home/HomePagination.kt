//package com.devstudio.dalua.ui.home.fragments.home
//
//import android.util.Log
//import androidx.annotation.NonNull
//import androidx.paging.PageKeyedDataSource
//import com.devstudio.dalua.models.ListAllAquariumResponse
//import java.util.*
//
//class HomePagination internal constructor(upcomingMatchesRepository: UpcomingMatchesRepository) :
//    PageKeyedDataSource<Int?, ListAllAquariumResponse.SharedAquariums?>() {
//    private val upcomingMatchesRepository: UpcomingMatchesRepository
//    override fun loadInitial(
//        @NonNull params: LoadInitialParams<Int?>,
//        @NonNull callback: LoadInitialCallback<Int?, SharedAquariums?>
//    ) {
//        Log.d(LOGGER, "loading Initial")
//        upcomingMatchesRepository.getUpcomingMatches(PAGE_KEY, PAGE_SIZE)
//            .subscribe(object : Observer<UpcomingMatchesAPIResponse?>() {
//                fun onSubscribe(d: Disposable) {
//                    Log.d(LOGGER, "onSubscribe of loadInitial")
//                }
//
//                fun onNext(upcomingMatchesAPIResponse: UpcomingMatchesAPIResponse) {
//                    Log.d(
//                        LOGGER,
//                        "onNext of loadInitial - " + upcomingMatchesAPIResponse.getData().size()
//                    )
//                    val upcomingMatches: List<SharedAquariums?> =
//                        processModelData(upcomingMatchesAPIResponse)
//                    callback.onResult(upcomingMatches, null, PAGE_KEY + PAGE_SIZE)
//                }
//
//                fun onError(e: Throwable) {
//                    Log.d(LOGGER, "onError of loadInitial - " + e.message)
//                }
//
//                fun onComplete() {
//                    Log.d(LOGGER, "onComplete of loadInitial")
//                }
//            })
//    }
//
//    override fun loadBefore(
//        @NonNull params: LoadParams<Int?>,
//        @NonNull callback: LoadCallback<Int?, SharedAquariums?>
//    ) {
//        Log.d(LOGGER, "loading before")
//        val newKey = if (PAGE_KEY > 0) PAGE_KEY - PAGE_SIZE else null
//        upcomingMatchesRepository.getUpcomingMatches(params.key, PAGE_SIZE)
//            .subscribe(object : Observer<UpcomingMatchesAPIResponse?>() {
//                fun onSubscribe(d: Disposable) {
//                    Log.d(LOGGER, "onSubscribe of loadBefore")
//                }
//
//                fun onNext(upcomingMatchesAPIResponse: UpcomingMatchesAPIResponse) {
//                    Log.d(LOGGER, "onNext of loadBefore")
//                    val upcomingMatches: List<SharedAquariums?> =
//                        processModelData(upcomingMatchesAPIResponse)
//                    callback.onResult(upcomingMatches, newKey)
//                }
//
//                fun onError(e: Throwable) {
//                    Log.d(LOGGER, "onError of loadBefore - " + e.message)
//                }
//
//                fun onComplete() {
//                    Log.d(LOGGER, "onComplete of loadBefore")
//                }
//            })
//    }
//
//    override fun loadAfter(
//        @NonNull params: LoadParams<Int?>,
//        @NonNull callback: LoadCallback<Int?, SharedAquariums?>
//    ) {
//        Log.d(LOGGER, "loading after")
//        upcomingMatchesRepository.getUpcomingMatches(params.key, PAGE_SIZE)
//            .subscribe(object : Observer<UpcomingMatchesAPIResponse?>() {
//                fun onSubscribe(d: Disposable) {
//                    Log.d(LOGGER, "onSubscribe of loadAfter")
//                }
//
//                fun onNext(upcomingMatchesAPIResponse: UpcomingMatchesAPIResponse) {
//                    Log.d(
//                        LOGGER,
//                        "onNext of loadAfter - " + upcomingMatchesAPIResponse.getData().size()
//                    )
//                    val upcomingMatches: List<ListAllAquariumResponse.SharedAquariums?> =
//                        processModelData(upcomingMatchesAPIResponse)
//                    val key =
//                        if (upcomingMatchesAPIResponse.isHasmore()) PAGE_KEY + PAGE_SIZE else null
//                    callback.onResult(upcomingMatches, key)
//                }
//
//                fun onError(e: Throwable) {
//                    Log.d(LOGGER, "onError of loadAfter - " + e.message)
//                }
//
//                fun onComplete() {
//                    Log.d(LOGGER, "onComplete of loadAfter")
//                }
//            })
//    }
//
//    companion object {
//        private val LOGGER = HomePagination::class.java.name
//        const val PAGE_SIZE = 5
//        private const val PAGE_KEY = 0
//    }
//
//    init {
//        this.upcomingMatchesRepository = upcomingMatchesRepository
//    }
//}