package ru.adonixis.vkcupdocs.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndlessRecyclerViewScrollListener : RecyclerView.OnScrollListener {
    private var visibleThreshold = 10
    private var currentPage = 0
    private var previousTotalItemCount = 0
    private var loading = true
    private val startingPageIndex = 0
    private val mLayoutManager: RecyclerView.LayoutManager
    private val itemsOnPageCount: Int

    constructor(layoutManager: LinearLayoutManager, itemsOnPageCount: Int) {
        mLayoutManager = layoutManager
        this.itemsOnPageCount = itemsOnPageCount
    }

    constructor(layoutManager: GridLayoutManager, itemsOnPageCount: Int) {
        mLayoutManager = layoutManager
        visibleThreshold = visibleThreshold * layoutManager.spanCount
        this.itemsOnPageCount = itemsOnPageCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager, itemsOnPageCount: Int) {
        mLayoutManager = layoutManager
        visibleThreshold = visibleThreshold * layoutManager.spanCount
        this.itemsOnPageCount = itemsOnPageCount
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCount = mLayoutManager.itemCount
        if (mLayoutManager is StaggeredGridLayoutManager) {
            val lastVisibleItemPositions = mLayoutManager.findLastVisibleItemPositions(null)
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
        } else if (mLayoutManager is GridLayoutManager) {
            lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition()
        } else if (mLayoutManager is LinearLayoutManager) {
            lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition()
        }
        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }
        if (loading && totalItemCount - previousTotalItemCount >= itemsOnPageCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }
        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, view)
            loading = true
        }
    }

    fun resetState() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
}