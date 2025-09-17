/*
 * Copyright 2021 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.airsaid.localization.translate.util

import java.util.function.BiConsumer

/**
 * @author airsaid
 */
class LRUCache<K, V>(initialCapacity: Int) {

    private val caches: MutableMap<K, Node<K, V>>
    private var head: Node<K, V>? = null
    private var tail: Node<K, V>? = null
    private var maxCapacity: Int

    init {
        maxCapacity = initialCapacity
        if (initialCapacity <= 0) {
            throw IllegalArgumentException("Illegal Capacity: $initialCapacity")
        }
        caches = linkedMapOf()
    }

    fun put(key: K, value: V) {
        while (isFull()) {
            removeTailNode()
        }
        val newNode = Node(key, value)
        caches[key] = newNode
        moveToHeadNode(newNode)
    }

    fun get(key: K?): V? {
        if (caches.containsKey(key)) {
            val newHead = caches[key]!!
            moveToHeadNode(newHead)
            return newHead.value
        }
        return null
    }

    fun size(): Int = caches.size

    fun isFull(): Boolean = size() > 0 && size() >= maxCapacity

    fun isEmpty(): Boolean = size() <= 0

    fun forEach(consumer: BiConsumer<K, V>) {
        for ((key, value) in caches) {
            consumer.accept(key, value.value)
        }
    }

    fun forEach(action: (K, V) -> Unit) {
        for ((key, value) in caches) {
            action(key, value.value)
        }
    }

    fun clear() {
        caches.clear()
        head = null
        tail = null
    }

    private fun moveToHeadNode(node: Node<K, V>) {
        if (head == null) {
            head = node
            tail = node
            return
        }

        node.next = head
        head?.prev = node
        head = node
    }

    private fun removeTailNode() {
        val currentTail = tail ?: return

        caches.remove(currentTail.key)
        val prev = currentTail.prev
        prev?.next = null
        currentTail.prev = null
        tail = prev
    }

    fun setMaxCapacity(maxCapacity: Int) {
        this.maxCapacity = maxCapacity
    }

    fun getMaxCapacity(): Int = maxCapacity

    private class Node<K, V>(
        val key: K,
        val value: V
    ) {
        var prev: Node<K, V>? = null
        var next: Node<K, V>? = null
    }
}