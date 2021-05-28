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

  private final int initialCapacity;

  public LRUCache(int initialCapacity) {
    this.initialCapacity = initialCapacity;
    if (initialCapacity <= 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    }
    caches = new LinkedHashMap<>(this.initialCapacity);
  }

  public void put(K key, V value) {
    if (isFull()) {
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
    return size() >= initialCapacity;
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
