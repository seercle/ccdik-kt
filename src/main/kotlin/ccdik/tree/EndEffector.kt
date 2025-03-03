package ccdik.tree

import ccdik.math.copy
import ccdik.math.toAxisAngle
import org.joml.Math
import org.joml.Quaterniond
import org.joml.Vector3d

class EndEffector(var parent: KinematicJoint?, var name: String, var localPosition: Vector3d) {

    val globalPosition: Vector3d = localPosition.copy()

    fun ccdik(goal: Vector3d, maxIteration: Int = 20, squaredTolerance: Double = .01) {
        var joint = this.parent
        val rotationTo = Quaterniond()

        outer@ for (r in 1..maxIteration) {
            while (joint != null) {

                // check if the goal is reached
                if (this.globalPosition.distanceSquared(goal) <= squaredTolerance) {
                    break@outer
                }

                // compute rotation
                val directionToEffector = this.globalPosition.copy().sub(joint.globalPosition)
                val directionToGoal = goal.copy().sub(joint.globalPosition)
                directionToEffector.rotationTo(directionToGoal, rotationTo)
                rotationTo.mul(joint.localRotation, joint.localRotation).normalize()

                // cast the rotation on the joint axis
                if (joint.axis != null) {
                    val currentAxis = joint.axis!!.copy().rotate(joint.localRotation)
                    currentAxis.rotationTo(joint.axis, rotationTo)
                    rotationTo.mul(joint.localRotation, joint.localRotation).normalize()
                }

                // clamp the angle
                if (joint.clampedAngleDegrees != null) {
                    val axisAngle = joint.localRotation.toAxisAngle()
                    axisAngle.angle =
                            Math.toRadians(
                                    Math.clamp(
                                            joint.clampedAngleDegrees!!.first,
                                            joint.clampedAngleDegrees!!.second,
                                            Math.toDegrees(axisAngle.angle)
                                    )
                            )
                    joint.localRotation
                            .rotationAxis(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z)
                            .normalize()
                }

                // update world values
                joint.updateWorldValues()

                joint = joint.parent
            }
        }
    }
}
