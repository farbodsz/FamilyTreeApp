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

package com.farbodsz.familytree.model.tree

/**
 * This class can be used when representing items in a tree in a list.
 *
 * @param T         the type of data being represented
 * @property data   the data for the item, of type [T]
 * @property depth  to display a tree like a list, the [depth] of the node must be known so that the
 *                  UI can be modified accordingly to show hierarchy.
 */
data class TreeListItem<out T>(val data: T, val depth: Int) {

    init {
        if (depth < 0) {
            throw IllegalArgumentException("the depth of a node in a tree must be at least 0")
        }
    }

}
