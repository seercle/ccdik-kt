# Cyclic Coordinate Descent (CCD) for Inverse Kinematics

## Usage
This CCD implementation uses a kinematic tree representation to solve the inverse kinematics problem.
The nodes of the tree are the joints of the kinematic chain, and the edges are named the end-effectors. 
The CCDIK algorithm is solved by giving a target position to an end-effector, and the algorithm will move the joints to reach the target position.

### Example
Let's build a kinematic tree of the following structure: left_effector <- left_joint2 <- left_joint1 <- root -> right_effector
```kotlin
//Define the elements of the kinematic tree to be accessible later
val root = KinematicJoint()
val left_joint1 = KinematicJoint()
val left_joint2 = KinematicJoint()
val left_effector = EndEffector()
val right_effector = EndEffector()

//Define the relative position of the joints and end-effectors
val root_position = ... // Set the position as a Vector3d
val left_joint1_position = ... // Set the position as a Vector3d, relative to the root
val left_joint2_position = ... // Set the position as a Vector3d, relative to the left_joint1
...

//Define the relative rotation of the joints
val root_rotation = ... // Set the rotation as a Quaterniond
val left_joint1_rotation = ... // Set the rotation as a Quaterniond, relative to the root
val left_joint2_rotation = ... // Set the rotation as a Quaterniond, relative to the left_joint1
...

//Define the rotation axis of the joints
val root_axis = null // The root joint has no rotation axis
val left_joint1_axis = Vector(.0, 1.0, .0) // The left_joint1 can only rotate around the y-axis
...

//Define the rotation constraints of the joints
val root_constraint = null // The root joint has no rotation constraints
val left_joint1_constraint = Pair(-90.0, 90.0) // The left_joint1 can only rotate between -90 and 90 degrees
...

//Create the kinematic tree using the KinematicTreeBuilder class
KinematicTreeBuilder("root", root_position, root_rotation, root_axis, root_constraint)
    .addNode(KinematicTreeBuilder("left_joint1", left_joint1_position, left_joint1_rotation, left_joint1_axis, left_joint1_constraint)
        .addNode(KinematicTreeBuilder("left_joint2", left_joint2_position, left_joint2_rotation, left_joint2_axis, left_joint2_constraint)
            .addEffector("left_effector", left_effector_position, left_effector)
            .build(left_joint2)
        )
        .build(left_joint1)
    )
    .addEffector("right_effector", right_effector_position, right_effector)
    .build(root)

target = ... // Set the target position as a Vector3d
left_effector.ccdik(target) // Move the joints to reach so that the left_effector reach the target position
```