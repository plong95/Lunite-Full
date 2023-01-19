package com.ruse.model.movement;

import java.util.ArrayDeque;
import java.util.Deque;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Direction;
import com.ruse.model.Locations;
import com.ruse.model.Locations.Location;
import com.ruse.model.Position;
import com.ruse.world.clip.region.DirectionFace;
import com.ruse.world.clip.region.RegionClipping;
import com.ruse.world.content.EnergyHandler;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.route.RouteFinder;
import com.ruse.world.entity.impl.player.route.strategy.EntityStrategy;

/**
 * A queue of {@link Direction}s which a {@link Character} will follow.
 * 
 * @author Graham Edgecombe Edited by Gabbe
 */
public final class MovementQueue {

	/**
	 * Represents a single point in the queue.
	 * 
	 * @author Graham Edgecombe
	 */
	private static final class Point {

		/**
		 * The point's position.
		 */
		private final Position position;

		/**
		 * The direction to walk to this point.
		 */
		private final Direction direction;

		/**
		 * Creates a point.
		 * 
		 * @param position  The position.
		 * @param direction The direction.
		 */
		public Point(Position position, Direction direction) {
			this.position = position;
			this.direction = direction;
		}

		@Override
		public String toString() {
			return Point.class.getName() + " [direction=" + direction + ", position=" + position + "]";
		}

	}

	/**
	 * The maximum size of the queue. If any additional steps are added, they are
	 * discarded.
	 */
	private static final int MAXIMUM_SIZE = 100;

	/**
	 * The character whose walking queue this is.
	 */
	private final Character character;

	/**
	 * The queue of directions.
	 */
	private final Deque<Point> points = new ArrayDeque<Point>();

	/**
	 * The following task
	 */
	private Task followTask;
	private Character followCharacter;

	/**
	 * Creates a walking queue for the specified character.
	 * 
	 * @param character The character.
	 */
	public MovementQueue(Character character) {
		this.character = character;
		this.isPlayer = character.isPlayer();
	}

	private final boolean isPlayer;

	/**
	 * Sets a character to follow
	 */
	public void setFollowCharacter(Character followCharacter) {
		this.followCharacter = followCharacter;
		//startFollow();
	}

	public Character getFollowCharacter() {
		return followCharacter;
	}

	/**
	 * Adds the first step to the queue, attempting to connect the server and client
	 * position by looking at the previous queue.
	 * 
	 * @param clientConnectionPosition The first step.
	 * @return {@code true} if the queues could be connected correctly,
	 *         {@code false} if not.
	 */
	public boolean addFirstStep(Position clientConnectionPosition) {
		reset();
		addStep(clientConnectionPosition);
		return true;
	}

	/**
	 * Adds a step to walk to the queue.
	 * 
	 * @param x       X to walk to
	 * @param y       Y to walk to
	 * @param clipped Can the step walk through objects?
	 */
	public void walkStep(int x, int y) {
		Position position = character.getPosition().copy();
		position.setX(position.getX() + x);
		position.setY(position.getY() + y);
		addStep(position);
	}

	/**
	 * Adds a step.
	 * 
	 * @param x           The x coordinate of this step.
	 * @param y           The y coordinate of this step.
	 * @param heightLevel
	 * @param flag
	 */
	private void addStep(int x, int y, int heightLevel) {
		if (character.getMovementQueue().isLockMovement() || character.isFrozen() || character.isStunned()) {
			return;
		}

		if (points.size() >= MAXIMUM_SIZE)
			return;

		final Point last = getLast();
		final int deltaX = x - last.position.getX();
		final int deltaY = y - last.position.getY();
		final Direction direction = Direction.fromDeltas(deltaX, deltaY);
		if (direction != Direction.NONE)
			points.add(new Point(new Position(x, y, heightLevel), direction));
	}

	/**
	 * Adds a step to the queue.
	 * 
	 * @param step The step to add.
	 * @oaram flag
	 */
	public void addStep(Position step) {
		if (character.isFrozen() || lockMovement || character.isStunned())
			return;
		final Point last = getLast();
		final int x = step.getX();
		final int y = step.getY();
		int deltaX = x - last.position.getX();
		int deltaY = y - last.position.getY();
		final int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));
		for (int i = 0; i < max; i++) {
			if (deltaX < 0)
				deltaX++;
			else if (deltaX > 0)
				deltaX--;
			if (deltaY < 0)
				deltaY++;
			else if (deltaY > 0)
				deltaY--;
			addStep(x - deltaX, y - deltaY, step.getZ());
		}
	}


	public void walk(Deque<Position> path) {
			if (character.isFrozen() || character.isStunned() || lockMovement)
				return;
		reset();
		for (Position position : path) {
				if (character.isFrozen() || character.isStunned() || character.getMovementQueue().lockMovement) {
					return;
				}
			if (points.size() >= MAXIMUM_SIZE) {
				return;
			}
			addStep(position);
		}
	}
	
	public boolean canWalk(int deltaX, int deltaY) {
		final Position to = new Position(character.getPosition().getX() + deltaX,
				character.getPosition().getY() + deltaY, character.getPosition().getZ());
		if (character.getPosition().getZ() == -1 && to.getZ() == -1 && character.isNpc()
				&& !((NPC) character).isSummoningNpc() || character.getLocation() == Location.RECIPE_FOR_DISASTER)
			return true;
		return canWalk(character.getPosition(), to, character.getSize());
	}

	/*public static boolean canWalk(Position from, Position to, int size) {
		return RegionClipping.canMove(from, to, size, size);
	}*/
/*

	public static boolean canWalk(Position from, Position to, int size) {
		DirectionFace direction = DirectionFace.Companion.between(from, to);
		if (direction == DirectionFace.NONE) {
			return false;
		}
		return RegionClipping.canWalk(from.getZ(), from.getX(), from.getY(), direction, size);
	}
*/

	public static boolean canWalk(Position from, Position to, int size) {
		return RegionClipping.canMove(from, to, size, size);
	}

	/*
	 * public boolean checkBarricade(int x, int y) { Position position =
	 * character.getPosition(); if(character.isPlayer()) {
	 * if(Locations.inSoulWars((Player)character)) {
	 * if(SoulWars.checkBarricade(position.getX() + x, position.getY()+ y,
	 * position.getZ())) { ((Player)character).getPacketSender().sendMessage(
	 * "The path is blocked by a Barricade."); reset(true); return true; } } }
	 * return false; }
	 */

	/**
	 * Gets the last point.
	 * 
	 * @return The last point.
	 */
	private Point getLast() {
		final Point last = points.peekLast();
		if (last == null)
			return new Point(character.getPosition(), Direction.NONE);
		return last;
	}

	/**
	 * @return true if the character is moving.
	 */
	public boolean isMoving() {
		return !points.isEmpty();
	}

	/**
	 * Called every 600ms, updates the queue.
	 */
	public void sequence() {

		boolean movement = !lockMovement && !character.isFrozen() && !character.isStunned();
		boolean movementOccured = false;
		Position previousPosition = null;

		if (character.isNpc()) {
			NPC n = ((NPC) character);
			if (((NPC) character).getId() == 6142 || ((NPC) character).getId() == 6143
					|| ((NPC) character).getId() == 6144  || ((NPC) character).getId() == 6145  || ((NPC) character).getId() == 9880
					|| ((NPC) character).getId() == 8013 || ((NPC) character).getId() == 9895

					|| ((NPC) character).getId() == 28060
					|| ((NPC) character).getId() == 9881
					|| ((NPC) character).getId() == 9882
					|| ((NPC) character).getId() == 9883
					|| ((NPC) character).getId() == 9884


					|| ((NPC) character).getId() == 27530
					|| ((NPC) character).getId() == 27606
					|| ((NPC) character).getId() == 27527
					|| ((NPC) character).getId() == 28263

					|| ((NPC) character).getId() == 1120
					|| ((NPC) character).getId() == 10014 || ((NPC) character).getId() == 28615 || ((NPC) character).getId() == 10000 || ((NPC) character).getId() == 10001 || ((NPC) character).getId() == 10015)
				return;
		}

		if (followCharacter != null) {
			handleFollowing();
		}
		
		if (movement) {
			Point walkPoint = null;
			Point runPoint = null;

			walkPoint = points.poll();
			if (isRunToggled()) {
				runPoint = points.poll();
			}

			if (character.isNeedsPlacement()) {
				reset();
				return;
			}

			if (walkPoint != null && walkPoint.direction != Direction.NONE) {

				if (followCharacter != null) {
					if (walkPoint.equals(followCharacter.getPosition())) {
						return;
					} else {
						if (!followCharacter.getMovementQueue().isRunToggled()) {
							if (character.getPosition().isWithinDistance(followCharacter.getPosition(), 2)) {
								runPoint = null;
							}
						}
					}
				}

				if (!isPlayer && !character.getCombatBuilder().isAttacking()) {
					if (((NPC) character).isSummoningNpc() && !((NPC) character).summoningCombat()) {
						if (!canWalk(character.getPosition(), walkPoint.position, character.getSize())) {
							return;
						}
					}
				}

				character.setPosition(walkPoint.position);
				character.setPrimaryDirection(walkPoint.direction);
				character.setLastDirection(walkPoint.direction);
			}
			if (runPoint != null && runPoint.direction != Direction.NONE) {
				if (followCharacter != null) {
					if (walkPoint.equals(followCharacter.getPosition())) {
						return;
					}
				}
				character.setPosition(runPoint.position);
				character.setSecondaryDirection(runPoint.direction);
				character.setLastDirection(runPoint.direction);
				if (isPlayer) {
					handleRegionChange();
				}
			}
		}

		if (isPlayer) {
			Locations.process(character);
			EnergyHandler.processPlayerEnergy((Player) character);
		}
	}


	public void handleFollowing() {

		// Check if we can still follow the leader.
		if (followCharacter == null || followCharacter.getConstitution() <= 0 || !followCharacter.isRegistered() || character.getConstitution() <= 0 || !character.isRegistered()) {
			character.setEntityInteraction(null);
			followCharacter = null;
			return;
		}
		if (!Location.ignoreFollowDistance(character)) {
			boolean summNpc = followCharacter.isPlayer() && character.isNpc() && ((NPC) character).isSummoningNpc();
			if (!character.getPosition().isWithinDistance(followCharacter.getPosition(), summNpc ? 10 : 20)) {
				character.setEntityInteraction(null);
				if (summNpc) {
					((Player) followCharacter).getSummoning().moveFollower(true);
				}
				return;
			}
		}

		// Update entity interaction
		character.setEntityInteraction(followCharacter);

			if (character.getMovementQueue().isLockMovement() || character.isFrozen() || character.isStunned()) {
				return;
			}


		// If we are on the same position as the leader then move away.
		if (character.getPosition().equals(followCharacter.getPosition()) || Locations.inside(character.getPosition(), character.getSize(), followCharacter.getPosition(), followCharacter.getSize())) {
			character.getMovementQueue().reset();
			if (character.isNpc() && ((NPC) character).getId() != 28386) {
				NPC npc = ((NPC) character);
				if (npc.isSummoningNpc()) {
					if (followCharacter.getMovementQueue().isMovementDone()) {
						MovementQueue.stepAway(character, followCharacter);
					}
				} else {
					if (followCharacter.getMovementQueue().isMovementDone()) {
						if (character.getCombatBuilder().isAttacking() && character.getMovementQueue().isMovementDone())
							MovementQueue.stepAway(character, followCharacter);
						else
							MovementQueue.stepAway(character);
					}
				}
			} else {
				if (followCharacter.getMovementQueue().isMovementDone()) {
					if (character.getCombatBuilder().isAttacking() && character.getMovementQueue().isMovementDone()) {
						if (followCharacter.getSize() > 1) {
							MovementQueue.stepAway(character, followCharacter);
						} else
							solveDiagonalBlock(character, followCharacter);
					} else
						MovementQueue.stepAway(character);
				}
			}

			return;
		}

		if (character.getCombatBuilder().getStrategy() == null) {
			character.getCombatBuilder().determineStrategy();
		}

		// Are we following in combat?
		boolean combatFollow = character.getCombatBuilder().isAttacking();

		// The amount of distance between us and the other character.
		int distance = character.getPosition().getDistance(followCharacter.getPosition());

		// Reset movement if we're in character size range.
		if (distance >= 50) {
			character.setEntityInteraction(null);
			followCharacter = null;
			reset();
			return;
		}

		if (combatFollow) {
			if (character.getCombatBuilder().getStrategy().getCombatType() == CombatType.MELEE && character.getSize() == 1 && followCharacter.getSize() == 1) {

				if (character.getPosition().equals(followCharacter.getPosition()) || isInDiagonalBlock(character, followCharacter)) {
					if (isMovementDone() && followCharacter.getMovementQueue().isMovementDone()) {
						solveDiagonalBlock(character, followCharacter);
						return;
					}
				}
			}

			if (CombatFactory.checkAttackDistance(character.getCombatBuilder())) {
				boolean isMeleeCombatStrategy = character.getCombatBuilder().getStrategy().getCombatType() == CombatType.MELEE;

				if (!isMeleeCombatStrategy) {
					reset();
				}
				return;
			}
		}

		Position destination = null;
		if (!combatFollow) {
			if (character.isPlayer() && followCharacter.isPlayer()) {
				Player p = (Player) followCharacter;
				if (p.getPreviousPosition() != null) {
					destination = p.getPreviousPosition();
					p.setPreviousPosition(null);
				}
			}
		}
		if (followCharacter != null && followCharacter.getPosition() != null)
			destination = followCharacter.getPosition();
		if (destination != null) {
			Character.walk(character, destination.getX(), destination.getY(), followCharacter.getSize());
		}
	}


	public boolean isMovementDone() {
		return points.size() == 0;
	}

	public void handleRegionChange() {
		final int diffX = character.getPosition().getX() - character.getLastKnownRegion().getRegionX() * 8;
		final int diffY = character.getPosition().getY() - character.getLastKnownRegion().getRegionY() * 8;
		boolean regionChanged = false;
		if (diffX < 16)
			regionChanged = true;
		else if (diffX >= 88)
			regionChanged = true;
		if (diffY < 16)
			regionChanged = true;
		else if (diffY >= 88)
			regionChanged = true;
		if (regionChanged) {
			((Player) character).getPacketSender().sendMapRegion();
		}
	}

	public void startFollow() {

		if (followCharacter == null && (followTask == null || !followTask.isRunning()))
			return;

		if (followTask == null || !followTask.isRunning()) {

			// Build the task that will be scheduled when following.
			followTask = new Task(1, character, true) {
				@Override
				public void execute() {

					// Check if we can still follow the leader.
					if (followCharacter == null || followCharacter.getConstitution() <= 0
							|| !followCharacter.isRegistered() || character.getConstitution() <= 0
							|| !character.isRegistered()) {
						character.setEntityInteraction(null);
						this.stop();
						return;
					}

					boolean combatFollowing = character.getCombatBuilder().isAttacking();
					if (!Location.ignoreFollowDistance(character)) {
						boolean summNpc = followCharacter.isPlayer() && character.isNpc()
								&& ((NPC) character).isSummoningNpc();
						if (!character.getPosition().isWithinDistance(followCharacter.getPosition(),
								summNpc ? 10 : combatFollowing ? 40 : 20)) {
							character.setEntityInteraction(null);
							this.stop();
							if (summNpc)
								((Player) followCharacter).getSummoning().moveFollower(true);
							return;
						}
					}

					// Block if our movement is locked.
					if (character.getMovementQueue().isLockMovement() || character.isFrozen()
							|| character.isStunned()) {
						return;
					}

					// If we are on the same position as the leader then move
					// away.

					// If combat follow, let the combat fcharactery handle it

					if (character.getPosition().equals(followCharacter.getPosition())) {
						character.getMovementQueue().reset();
						if (followCharacter.getMovementQueue().isMovementDone())
							MovementQueue.stepAway(character);
						return;
					}

					// Check if we are within distance to attack for combat.
					if (combatFollowing) {
						// if (character.isPlayer()) {
						if (character.getCombatBuilder().getStrategy() == null) {
							character.getCombatBuilder().determineStrategy();
						}

						if (character.getCombatBuilder().getStrategy().getCombatType() == CombatType.MELEE && character.getSize() == 1 && followCharacter.getSize() == 1) {

							if (character.getPosition().equals(followCharacter.getPosition()) || isInDiagonalBlock(character, followCharacter)) {
								if (isMovementDone() && followCharacter.getMovementQueue().isMovementDone()) {
									solveDiagonalBlock(character, followCharacter);
									return;
								}
							}
						}


						if (CombatFactory.checkAttackDistance(character.getCombatBuilder())) {
							boolean isMeleeCombatStrategy = character.getCombatBuilder().getStrategy().getCombatType() == CombatType.MELEE;
							if (!isMeleeCombatStrategy) {
								reset();
							}
							return;
						}
					} else {
						if (character.getInteractingEntity() != followCharacter) {
							character.setEntityInteraction(followCharacter);
						}
					}

					// If we are within 1 square we don't need to move.
					if (Locations.goodDistance(character.getPosition(), followCharacter.getPosition(), 1)) {
						return;
					}

					if (character.isNpc() && ((NPC) character).isSummoningNpc()
							&& (followCharacter.getLocation() == Location.HOME_BANK
									|| followCharacter.getLocation() == Location.EDGEVILLE
									|| followCharacter.getLocation() == Location.VARROCK)) {
						character.getMovementQueue().walkStep(
								getMove(character.getPosition().getX(), followCharacter.getPosition().getX(), 1),
								getMove(character.getPosition().getY() - 1, followCharacter.getPosition().getY(), 1));
					} else {
						PathFinder.findPath(character, followCharacter.getPosition().getX(),
								followCharacter.getPosition().getY() - character.getSize(), true, character.getSize(),
								character.getSize());
					}
				}

				@Override
				public void stop() {
					setEventRunning(false);
					followTask = null;
				}
			};

			// Then submit the actual task.
			TaskManager.submit(followTask);
		}
	}

	/**
	 * Stops the movement.
	 */
	public MovementQueue reset() {
		points.clear();
		return this;
	}

	/**
	 * Gets the size of the queue.
	 * 
	 * @return The size of the queue.
	 */
	public int size() {
		return points.size();
	}

	/**
	 * The force movement array index values.
	 */
	public static final int FIRST_MOVEMENT_X = 0, FIRST_MOVEMENT_Y = 1, SECOND_MOVEMENT_X = 2, SECOND_MOVEMENT_Y = 3,
			MOVEMENT_SPEED = 4, MOVEMENT_REVERSE_SPEED = 5, MOVEMENT_DIRECTION = 6;

	/**
	 * Steps away from a Gamecharacter
	 * 
	 * @param character The gamecharacter to step away from
	 */
	public static void stepAway(Character character) {
		if (character.getMovementQueue().canWalk(-1, 0))
			character.getMovementQueue().walkStep(-1, 0);
		else if (character.getMovementQueue().canWalk(1, 0))
			character.getMovementQueue().walkStep(1, 0);
		else if (character.getMovementQueue().canWalk(0, -1))
			character.getMovementQueue().walkStep(0, -1);
		else if (character.getMovementQueue().canWalk(0, 1))
			character.getMovementQueue().walkStep(0, 1);
	}

	public static Position stepAway(Character actor, Character victim) {
		return actor.getMovementQueue().surround(victim);
	}

	public void stun(int delay) {
		character.setStunDelay(delay);
		if (character.isPlayer()) {
			((Player) character).getPacketSender().sendMessage("You have been stunned!");
		}
		reset();
		TaskManager.submit(new Task(2, character, true) {
			@Override
			protected void execute() {
				if (!character.isRegistered() || character.getConstitution() <= 0) {
					stop();
					return;
				}
				if (character.decrementAndGetStunDelay() == 0) {
					stop();
				}
			}
		});
	}

	public void freeze(int delay) {
		if (character.isFrozen())
			return;
		character.setFreezeDelay(delay);
		if (character.isPlayer()) {
			((Player) character).getPacketSender().sendMessage("You have been frozen!");
		}
		reset();
		TaskManager.submit(new Task(2, character, true) {
			@Override
			protected void execute() {
				if (!character.isRegistered() || character.getConstitution() <= 0) {
					stop();
					return;
				}
				if (character.decrementAndGetFreezeDelay() == 0) {
					stop();
				}
			}
		});
	}

	public static int getMove(int x, int p2, int size) {
		if ((x - p2) == 0) {
			return 0;
		} else if ((x - p2) < 0) {
			return size;
		} else if ((x - p2) > 0) {
			return -size;
		}
		return 0;
	}

	/** If this entity's movement is locked. */
	private boolean lockMovement;

	/**
	 * Gets whether or not this entity is 'frozen'.
	 * 
	 * @return true if this entity cannot move.
	 */
	public boolean isLockMovement() {
		return lockMovement;
	}

	/**
	 * Sets if this entity can move or not.
	 * 
	 * @param lockMovement true if this entity cannot move.
	 */
	public MovementQueue setLockMovement(boolean lockMovement) {
		this.lockMovement = lockMovement;
		return this;
	}

	public boolean isRunToggled() {
		return character.isPlayer() && ((Player) character).isRunning() && !((Player) character).isCrossingObstacle();
	}


	public Position surround(Character target) {
		if (character.getPosition().getZ() != target.getPosition().getZ()) {
			return null;
		}
		int steps = RouteFinder.findRoute(character.getPosition().getX(), character.getPosition().getY(), character.getPosition().getZ(), character.getSize(), new EntityStrategy(target), true);
		if (steps < 1) {
			return null;
		}
		int[] bufferX = RouteFinder.getLastPathBufferX();
		int[] bufferY = RouteFinder.getLastPathBufferY();
		points.clear();
		Position last = null;
		for (int step = steps - 1; step >= 0; step--) {
			Position position = new Position(bufferX[step], bufferY[step], character.getPosition().getZ());
			if (!addStep1(position)) {
				break;
			}
			last = position;
		}
		return last;
	}


	public boolean addStep1(Position dest) {
		Position lastPosition = getLast().position;
		int currentX = lastPosition.getX();
		int currentY = lastPosition.getY();
		while (points.size() < MAXIMUM_SIZE && canStep()) {
			if (currentX < dest.getX()) {
				currentX++;
			} else if (currentX > dest.getX()) {
				currentX--;
			}
			if (currentY < dest.getY()) {
				currentY++;
			} else if (currentY > dest.getY()) {
				currentY--;
			}
			int deltaX = currentX - lastPosition.getX();
			int deltaY = currentY - lastPosition.getY();
			Direction direction = Direction.fromDeltas(deltaX, deltaY);
			Position newPosition = new Position(currentX, currentY, dest.getZ());
			if (direction != Direction.NONE) {
				points.add(new Point(newPosition, direction));
			}
			if (currentX == dest.getX() && currentY == dest.getY()) {
				return true;
			}
			lastPosition = newPosition;
		}
		return false;
	}
	private boolean canStep() {
		return !character.isFrozen() && !character.isStunned() && !lockMovement;
	}

	public final static boolean isInDiagonalBlock(Character attacker, Character attacked) {
		return attacked.getPosition().getX() - 1 == attacker.getPosition().getX() && attacked.getPosition().getY() + 1 == attacker.getPosition().getY()
				|| attacker.getPosition().getX() - 1 == attacked.getPosition().getX() && attacker.getPosition().getY() + 1 == attacked.getPosition().getY()
				|| attacked.getPosition().getX() + 1 == attacker.getPosition().getX() && attacked.getPosition().getY() - 1 == attacker.getPosition().getY()
				|| attacker.getPosition().getX() + 1 == attacked.getPosition().getX() && attacker.getPosition().getY() - 1 == attacked.getPosition().getY()
				|| attacked.getPosition().getX() + 1 == attacker.getPosition().getX() && attacked.getPosition().getY() + 1 == attacker.getPosition().getY()
				|| attacker.getPosition().getX() + 1 == attacked.getPosition().getX() && attacker.getPosition().getY() + 1 == attacked.getPosition().getY();
	}


	static void solveDiagonalBlock(Character attacker, Character target) {
		int aX = attacker.getPosition().getX();
		int aY = attacker.getPosition().getY();

		int vX = target.getPosition().getX();
		int vY = target.getPosition().getY();

		if (aX > vX && RegionClipping.canWalk(attacker.getPosition(), Direction.WEST, attacker.getSize())) {
			attacker.getMovementQueue().walkStep(-1, 0);
		} else if (vX > aX && RegionClipping.canWalk(attacker.getPosition(), Direction.EAST, attacker.getSize())) {
			attacker.getMovementQueue().walkStep(1, 0);
		} else if (aY > vY && RegionClipping.canWalk(attacker.getPosition(), Direction.SOUTH, attacker.getSize())) {
			attacker.getMovementQueue().walkStep(0, -1);
		} else if (vY > aY && RegionClipping.canWalk(attacker.getPosition(), Direction.NORTH, attacker.getSize())) {
			attacker.getMovementQueue().walkStep(0, 1);
		}

	}


}