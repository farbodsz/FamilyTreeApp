/*
 * Copyright 2018 Farbod Salamat-Zadeh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farbodsz.familytree.ui

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Class to dynamically populate pages inside of [ViewPager].
 */
class DynamicPagerAdapter : PagerAdapter() {

    private val views = ArrayList<View>()
    private val titles = ArrayList<String>()

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container!!.removeView(views[position])
    }

    override fun getCount() = views.size

    override fun getItemPosition(obj: Any?): Int {
        val index = views.indexOf(obj)
        return if (index == -1) {
            POSITION_NONE
        } else {
            index
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        val l = Locale.getDefault()
        return titles[position].toUpperCase(l)
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val view = views[position]
        container!!.addView(view)
        return view
    }

    override fun isViewFromObject(view: View?, obj: Any?) = view == obj

    @JvmOverloads
    fun addView(view: View, position: Int = views.size) = addViewWithTitle(view, position, "")

    @JvmOverloads
    fun addViewWithTitle(view: View, position: Int = views.size, title: String): Int {
        views.add(position, view)
        titles.add(title)
        notifyDataSetChanged()
        return position
    }

    fun removeView(pager: ViewPager, position: Int): Int {
        pager.adapter = null
        views.removeAt(position)
        pager.adapter = this
        return position
    }

}
