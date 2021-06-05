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

package com.airsaid.localization.translate.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author airsaid
 */
public class LRUCache<K, V> {

  private final Map<K, Node<K, V>> caches;
  private Node<K, V> head;
  private Node<K, V> tail;

  private int maxCapacity;

  public LRUCache(int initialCapacity) {
    maxCapacity = initialCapacity;
    if (initialCapacity <= 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    }
    caches = new LinkedHashMap<>(initialCapacity);
  }

  public void put(K key, V value) {
    while (isFull()) {
      removeTailNode();
    }
    Node<K, V> newNode = new Node<>(key, value);
    caches.put(key, newNode);
    moveToHeadNode(newNode);
  }

  public V get(K key) {
    if (caches.containsKey(key)) {
      Node<K, V> newHead = caches.get(key);
      moveToHeadNode(newHead);
      return newHead.value;
    }
    return null;
  }

  public int size() {
    return caches.size();
  }

  public boolean isFull() {
    return size() > 0 && size() >= maxCapacity;
  }

  public boolean isEmpty() {
    return size() <= 0;
  }

  public void forEach(BiConsumer<K, V> consumer) {
    for (Map.Entry<K, Node<K, V>> entry : caches.entrySet()) {
      K key = entry.getKey();
      Node<K, V> value = entry.getValue();
      consumer.accept(key, value.value);
    }
  }

  public void clear() {
    caches.clear();
    head = null;
    tail = null;
  }

  private void moveToHeadNode(Node<K, V> node) {
    if (head == null) {
      head = node;
      tail = node;
      return;
    }

    node.next = head;
    head.prev = node;
    head = node;
  }

  private void removeTailNode() {
    if (tail == null) return;

    caches.remove(tail.key);
    Node<K, V> prev = tail.prev;
    if (prev != null) {
      prev.next = null;
      tail.prev = null;
    }
    tail = prev;
  }

  public void setMaxCapacity(int maxCapacity) {
    this.maxCapacity = maxCapacity;
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }

  private static class Node<K, V> {
    public K key;
    public V value;
    public Node<K, V> prev;
    public Node<K, V> next;

    public Node(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

}
