package util

/**
 * Observer.scala
 *
 * A trait and a class for implementing the Observer pattern.
 * The trait `Observer` defines the contract for an observer,
 * while the class `Observable` provides functionality to manage observers.
 */

/**
 * Observer trait
 *
 * Defines the contract for an observer.
 * Implementing classes should provide an `update` method
 * that gets called when the observed subject changes.
 */
trait Observer {
  def update: Unit
}

/**
 * Observable class
 *
 * Provides functionality to manage observers and notify them of changes.
 * Observers can be added, removed, and the `notifyObservers` method can be called
 * to notify all subscribed observers.
 */
class Observable:
  var subscribers: Vector[Observer] = Vector()

  /**
   * Adds an observer to the list of subscribers.
   *
   * @param s The observer to be added.
   */
  def add(s: Observer): Unit = subscribers = subscribers :+ s

  /**
   * Removes an observer from the list of subscribers.
   *
   * @param s The observer to be removed.
   */
  def remove(s: Observer): Unit = subscribers = subscribers.filterNot(o => o == s)

  /**
   * Notifies all subscribed observers by calling their `update` method.
   */
  def notifyObservers: Unit = subscribers.foreach(o => o.update)
