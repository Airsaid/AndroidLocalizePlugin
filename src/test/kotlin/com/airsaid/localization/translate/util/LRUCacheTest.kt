package com.airsaid.localization.translate.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author airsaid
 */
class LRUCacheTest {

  @Test
  fun testEmpty() {
    val lruCache = LRUCache<String, String>(10)
    assertTrue(lruCache.isEmpty())
    assertFalse(lruCache.isFull())
    assertNull(lruCache.get("key"))
  }

  @Test
  fun testFull() {
    val lruCache = LRUCache<String, String>(1)
    lruCache.put("key", "value")
    assertFalse(lruCache.isEmpty())
    assertTrue(lruCache.isFull())
    assertNotNull(lruCache.get("key"))
  }

  @Test
  fun testPut() {
    val lruCache = LRUCache<String, String>(3)
    lruCache.put("key1", "value1")
    lruCache.put("key2", "value2")
    lruCache.put("key3", "value3")
    lruCache.put("key4", "value4")
    assertNull(lruCache.get("key1"))
    assertEquals("value2", lruCache.get("key2"))
    assertEquals("value3", lruCache.get("key3"))
    assertEquals("value4", lruCache.get("key4"))
    lruCache.put("key5", "value5")
    assertNull(lruCache.get("key2"))
  }
}