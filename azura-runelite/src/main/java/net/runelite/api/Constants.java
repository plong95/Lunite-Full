/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.api;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.beans.Transient;
import java.lang.ref.Cleaner;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * A utility class containing constant values.
 */
public class Constants
{
	/**
	 * The original width of the game when running in fixed mode.
	 */
	public static final int GAME_FIXED_WIDTH = 765;

	/**
	 * The original height of the game when running in fixed mode.
	 */
	public static final int GAME_FIXED_HEIGHT = 503;

	/**
	 * Dimension representation of the width and height of the game in fixed mode.
	 */
	public static final Dimension GAME_FIXED_SIZE = new Dimension(GAME_FIXED_WIDTH, GAME_FIXED_HEIGHT);

	/**
	 * The aspect ratio of the game when running in fixed mode.
	 */
	public static final double GAME_FIXED_ASPECT_RATIO = (double) GAME_FIXED_WIDTH / (double) GAME_FIXED_HEIGHT;

	/**
	 * The default camera zoom value.
	 */
	public static final int CLIENT_DEFAULT_ZOOM = 512;

	/**
	 * The width and length of a chunk (8x8 tiles).
	 */
	public static final int CHUNK_SIZE = 8;

	/**
	 * The width and length of a map region (64x64 tiles).
	 */
	public static final int REGION_SIZE = 64;

	/**
	 * The width and length of the scene (13 chunks x 8 tiles).
	 */
	public static final int SCENE_SIZE = 104;

	/**
	 * The max allowed plane by the game.
	 * <p>
	 * This value is exclusive. The plane is set by 2 bits which restricts
	 * the plane value to 0-3.
	 */
	public static final int MAX_Z = 4;

	public static final int TILE_FLAG_BRIDGE = 2;

	/**
	 * The number of milliseconds in a client tick.
	 * <p>
	 * This is the length of a single frame when the client is running at
	 * the maximum framerate of 50 fps.
	 */
	public static final int CLIENT_TICK_LENGTH = 20;

	/**
	 * The number of milliseconds in a server game tick.
	 * <p>
	 * This is the length of a single game cycle under ideal conditions.
	 * All game-play actions operate within multiples of this duration.
	 */
	public static final int GAME_TICK_LENGTH = 600;

	/**
	 * Width of a standard item sprite
	 */
	public static final int ITEM_SPRITE_WIDTH = 36;

	/**
	 * Height of a standard item sprite
	 */
	public static final int ITEM_SPRITE_HEIGHT = 32;

	/**
	 * High alchemy = shop price * HIGH_ALCHEMY_MULTIPLIER
	 *
	 * @see ItemComposition#getPrice
	 */
	public static final float HIGH_ALCHEMY_MULTIPLIER = .6f;

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.2
	 */
	public static double getWidth() {
		return GAME_FIXED_SIZE.getWidth();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.2
	 */
	public static double getHeight() {
		return GAME_FIXED_SIZE.getHeight();
	}

	/**
	 * Sets the size of this {@code Dimension} object to
	 * the specified width and height in double precision.
	 * Note that if {@code width} or {@code height}
	 * are larger than {@code Integer.MAX_VALUE}, they will
	 * be reset to {@code Integer.MAX_VALUE}.
	 *
	 * @param width  the new width for the {@code Dimension} object
	 * @param height the new height for the {@code Dimension} object
	 * @since 1.2
	 */
	public static void setSize(double width, double height) {
		GAME_FIXED_SIZE.setSize(width, height);
	}

	/**
	 * Gets the size of this {@code Dimension} object.
	 * This method is included for completeness, to parallel the
	 * {@code getSize} method defined by {@code Component}.
	 *
	 * @return the size of this dimension, a new instance of
	 * {@code Dimension} with the same width and height
	 * @see Dimension#setSize
	 * @see Component#getSize
	 * @since 1.1
	 */
	@Transient
	public static Dimension getSize() {
		return GAME_FIXED_SIZE.getSize();
	}

	/**
	 * Sets the size of this {@code Dimension} object to the specified size.
	 * This method is included for completeness, to parallel the
	 * {@code setSize} method defined by {@code Component}.
	 *
	 * @param d the new size for this {@code Dimension} object
	 * @see Dimension#getSize
	 * @see Component#setSize
	 * @since 1.1
	 */
	public static void setSize(Dimension d) {
		GAME_FIXED_SIZE.setSize(d);
	}

	/**
	 * Sets the size of this {@code Dimension} object
	 * to the specified width and height.
	 * This method is included for completeness, to parallel the
	 * {@code setSize} method defined by {@code Component}.
	 *
	 * @param width  the new width for this {@code Dimension} object
	 * @param height the new height for this {@code Dimension} object
	 * @see Dimension#getSize
	 * @see Component#setSize
	 * @since 1.1
	 */
	public static void setSize(int width, int height) {
		GAME_FIXED_SIZE.setSize(width, height);
	}

	/**
	 * Sets the size of this {@code Dimension2D} object to
	 * match the specified size.
	 * This method is included for completeness, to parallel the
	 * {@code getSize} method of {@code Component}.
	 *
	 * @param d the new size for the {@code Dimension2D}
	 *          object
	 * @since 1.2
	 */
	public static void setSize(Dimension2D d) {
		GAME_FIXED_SIZE.setSize(d);
	}

	/**
	 * Constructs a new object.
	 */
	public Constants() {
		super();
	}

	/**
	 * Returns a hash code value for the object. This method is
	 * supported for the benefit of hash tables such as those provided by
	 * {@link HashMap}.
	 * <p>
	 * The general contract of {@code hashCode} is:
	 * <ul>
	 * <li>Whenever it is invoked on the same object more than once during
	 *     an execution of a Java application, the {@code hashCode} method
	 *     must consistently return the same integer, provided no information
	 *     used in {@code equals} comparisons on the object is modified.
	 *     This integer need not remain consistent from one execution of an
	 *     application to another execution of the same application.
	 * <li>If two objects are equal according to the {@code equals(Object)}
	 *     method, then calling the {@code hashCode} method on each of
	 *     the two objects must produce the same integer result.
	 * <li>It is <em>not</em> required that if two objects are unequal
	 *     according to the {@link Object#equals(Object)}
	 *     method, then calling the {@code hashCode} method on each of the
	 *     two objects must produce distinct integer results.  However, the
	 *     programmer should be aware that producing distinct integer results
	 *     for unequal objects may improve the performance of hash tables.
	 * </ul>
	 * <p>
	 * As much as is reasonably practical, the hashCode method defined
	 * by class {@code Object} does return distinct integers for
	 * distinct objects. (The hashCode may or may not be implemented
	 * as some function of an object's memory address at some point
	 * in time.)
	 *
	 * @return a hash code value for this object.
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * <p>
	 * The {@code equals} method implements an equivalence relation
	 * on non-null object references:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any non-null reference value
	 *     {@code x}, {@code x.equals(x)} should return
	 *     {@code true}.
	 * <li>It is <i>symmetric</i>: for any non-null reference values
	 *     {@code x} and {@code y}, {@code x.equals(y)}
	 *     should return {@code true} if and only if
	 *     {@code y.equals(x)} returns {@code true}.
	 * <li>It is <i>transitive</i>: for any non-null reference values
	 *     {@code x}, {@code y}, and {@code z}, if
	 *     {@code x.equals(y)} returns {@code true} and
	 *     {@code y.equals(z)} returns {@code true}, then
	 *     {@code x.equals(z)} should return {@code true}.
	 * <li>It is <i>consistent</i>: for any non-null reference values
	 *     {@code x} and {@code y}, multiple invocations of
	 *     {@code x.equals(y)} consistently return {@code true}
	 *     or consistently return {@code false}, provided no
	 *     information used in {@code equals} comparisons on the
	 *     objects is modified.
	 * <li>For any non-null reference value {@code x},
	 *     {@code x.equals(null)} should return {@code false}.
	 * </ul>
	 * <p>
	 * The {@code equals} method for class {@code Object} implements
	 * the most discriminating possible equivalence relation on objects;
	 * that is, for any non-null reference values {@code x} and
	 * {@code y}, this method returns {@code true} if and only
	 * if {@code x} and {@code y} refer to the same object
	 * ({@code x == y} has the value {@code true}).
	 * <p>
	 * Note that it is generally necessary to override the {@code hashCode}
	 * method whenever this method is overridden, so as to maintain the
	 * general contract for the {@code hashCode} method, which states
	 * that equal objects must have equal hash codes.
	 *
	 * @param obj the reference object with which to compare.
	 * @return {@code true} if this object is the same as the obj
	 * argument; {@code false} otherwise.
	 * @see #hashCode()
	 * @see HashMap
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/**
	 * Creates and returns a copy of this object.  The precise meaning
	 * of "copy" may depend on the class of the object. The general
	 * intent is that, for any object {@code x}, the expression:
	 * <blockquote>
	 * <pre>
	 * x.clone() != x</pre></blockquote>
	 * will be true, and that the expression:
	 * <blockquote>
	 * <pre>
	 * x.clone().getClass() == x.getClass()</pre></blockquote>
	 * will be {@code true}, but these are not absolute requirements.
	 * While it is typically the case that:
	 * <blockquote>
	 * <pre>
	 * x.clone().equals(x)</pre></blockquote>
	 * will be {@code true}, this is not an absolute requirement.
	 * <p>
	 * By convention, the returned object should be obtained by calling
	 * {@code super.clone}.  If a class and all of its superclasses (except
	 * {@code Object}) obey this convention, it will be the case that
	 * {@code x.clone().getClass() == x.getClass()}.
	 * <p>
	 * By convention, the object returned by this method should be independent
	 * of this object (which is being cloned).  To achieve this independence,
	 * it may be necessary to modify one or more fields of the object returned
	 * by {@code super.clone} before returning it.  Typically, this means
	 * copying any mutable objects that comprise the internal "deep structure"
	 * of the object being cloned and replacing the references to these
	 * objects with references to the copies.  If a class contains only
	 * primitive fields or references to immutable objects, then it is usually
	 * the case that no fields in the object returned by {@code super.clone}
	 * need to be modified.
	 * <p>
	 * The method {@code clone} for class {@code Object} performs a
	 * specific cloning operation. First, if the class of this object does
	 * not implement the interface {@code Cloneable}, then a
	 * {@code CloneNotSupportedException} is thrown. Note that all arrays
	 * are considered to implement the interface {@code Cloneable} and that
	 * the return type of the {@code clone} method of an array type {@code T[]}
	 * is {@code T[]} where T is any reference or primitive type.
	 * Otherwise, this method creates a new instance of the class of this
	 * object and initializes all its fields with exactly the contents of
	 * the corresponding fields of this object, as if by assignment; the
	 * contents of the fields are not themselves cloned. Thus, this method
	 * performs a "shallow copy" of this object, not a "deep copy" operation.
	 * <p>
	 * The class {@code Object} does not itself implement the interface
	 * {@code Cloneable}, so calling the {@code clone} method on an object
	 * whose class is {@code Object} will result in throwing an
	 * exception at run time.
	 *
	 * @return a clone of this instance.
	 * @throws CloneNotSupportedException if the object's class does not
	 *                                    support the {@code Cloneable} interface. Subclasses
	 *                                    that override the {@code clone} method can also
	 *                                    throw this exception to indicate that an instance cannot
	 *                                    be cloned.
	 * @see Cloneable
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Returns a string representation of the object. In general, the
	 * {@code toString} method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommended that all subclasses override this method.
	 * <p>
	 * The {@code toString} method for class {@code Object}
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `{@code @}', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object. In other words, this method returns a string equal to the
	 * value of:
	 * <blockquote>
	 * <pre>
	 * getClass().getName() + '@' + Integer.toHexString(hashCode())
	 * </pre></blockquote>
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * Called by the garbage collector on an object when garbage collection
	 * determines that there are no more references to the object.
	 * A subclass overrides the {@code finalize} method to dispose of
	 * system resources or to perform other cleanup.
	 * <p>
	 * The general contract of {@code finalize} is that it is invoked
	 * if and when the Java&trade; virtual
	 * machine has determined that there is no longer any
	 * means by which this object can be accessed by any thread that has
	 * not yet died, except as a result of an action taken by the
	 * finalization of some other object or class which is ready to be
	 * finalized. The {@code finalize} method may take any action, including
	 * making this object available again to other threads; the usual purpose
	 * of {@code finalize}, however, is to perform cleanup actions before
	 * the object is irrevocably discarded. For example, the finalize method
	 * for an object that represents an input/output connection might perform
	 * explicit I/O transactions to break the connection before the object is
	 * permanently discarded.
	 * <p>
	 * The {@code finalize} method of class {@code Object} performs no
	 * special action; it simply returns normally. Subclasses of
	 * {@code Object} may override this definition.
	 * <p>
	 * The Java programming language does not guarantee which thread will
	 * invoke the {@code finalize} method for any given object. It is
	 * guaranteed, however, that the thread that invokes finalize will not
	 * be holding any user-visible synchronization locks when finalize is
	 * invoked. If an uncaught exception is thrown by the finalize method,
	 * the exception is ignored and finalization of that object terminates.
	 * <p>
	 * After the {@code finalize} method has been invoked for an object, no
	 * further action is taken until the Java virtual machine has again
	 * determined that there is no longer any means by which this object can
	 * be accessed by any thread that has not yet died, including possible
	 * actions by other objects or classes which are ready to be finalized,
	 * at which point the object may be discarded.
	 * <p>
	 * The {@code finalize} method is never invoked more than once by a Java
	 * virtual machine for any given object.
	 * <p>
	 * Any exception thrown by the {@code finalize} method causes
	 * the finalization of this object to be halted, but is otherwise
	 * ignored.
	 *
	 * @throws Throwable the {@code Exception} raised by this method
	 * @apiNote Classes that embed non-heap resources have many options
	 * for cleanup of those resources. The class must ensure that the
	 * lifetime of each instance is longer than that of any resource it embeds.
	 * {@link Reference#reachabilityFence} can be used to ensure that
	 * objects remain reachable while resources embedded in the object are in use.
	 * <p>
	 * A subclass should avoid overriding the {@code finalize} method
	 * unless the subclass embeds non-heap resources that must be cleaned up
	 * before the instance is collected.
	 * Finalizer invocations are not automatically chained, unlike constructors.
	 * If a subclass overrides {@code finalize} it must invoke the superclass
	 * finalizer explicitly.
	 * To guard against exceptions prematurely terminating the finalize chain,
	 * the subclass should use a {@code try-finally} block to ensure
	 * {@code super.finalize()} is always invoked. For example,
	 * <pre>{@code      @Override
	 *     protected void finalize() throws Throwable {
	 *         try {
	 *             ... // cleanup subclass state
	 *         } finally {
	 *             super.finalize();
	 *         }
	 *     }
	 * }</pre>
	 * @jls 12.6 Finalization of Class Instances
	 * @see WeakReference
	 * @see PhantomReference
	 * @deprecated The finalization mechanism is inherently problematic.
	 * Finalization can lead to performance issues, deadlocks, and hangs.
	 * Errors in finalizers can lead to resource leaks; there is no way to cancel
	 * finalization if it is no longer necessary; and no ordering is specified
	 * among calls to {@code finalize} methods of different objects.
	 * Furthermore, there are no guarantees regarding the timing of finalization.
	 * The {@code finalize} method might be called on a finalizable object
	 * only after an indefinite delay, if at all.
	 * <p>
	 * Classes whose instances hold non-heap resources should provide a method
	 * to enable explicit release of those resources, and they should also
	 * implement {@link AutoCloseable} if appropriate.
	 * The {@link Cleaner} and {@link PhantomReference}
	 * provide more flexible and efficient ways to release resources when an object
	 * becomes unreachable.
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
