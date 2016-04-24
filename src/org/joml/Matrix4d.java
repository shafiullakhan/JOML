/*
 * (C) Copyright 2015-2016 Richard Greenlees

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Contains the definition of a 4x4 Matrix of doubles, and associated functions to transform
 * it. The matrix is column-major to match OpenGL's interpretation, and it looks like this:
 * <p>
 *      m00  m10  m20  m30<br>
 *      m01  m11  m21  m31<br>
 *      m02  m12  m22  m32<br>
 *      m03  m13  m23  m33<br>
 * 
 * @author Richard Greenlees
 * @author Kai Burjack
 */
public class Matrix4d implements Externalizable {

    private static final long serialVersionUID = 1L;

    public static final int M00 = 0;
    public static final int M01 = 1;
    public static final int M02 = 2;
    public static final int M03 = 3;
    public static final int M10 = 4;
    public static final int M11 = 5;
    public static final int M12 = 6;
    public static final int M13 = 7;
    public static final int M20 = 8;
    public static final int M21 = 9;
    public static final int M22 = 10;
    public static final int M23 = 11;
    public static final int M30 = 12;
    public static final int M31 = 13;
    public static final int M32 = 14;
    public static final int M33 = 15;

    /**
     * Argument to the first parameter of {@link #frustumPlane(int, Vector4d)}
     * identifying the plane with equation <tt>x=-1</tt> when using the identity matrix.  
     */
    public static final int PLANE_NX = 0;
    /**
     * Argument to the first parameter of {@link #frustumPlane(int, Vector4d)}
     * identifying the plane with equation <tt>x=1</tt> when using the identity matrix.  
     */
    public static final int PLANE_PX = 1;
    /**
     * Argument to the first parameter of {@link #frustumPlane(int, Vector4d)}
     * identifying the plane with equation <tt>y=-1</tt> when using the identity matrix.  
     */
    public static final int PLANE_NY= 2;
    /**
     * Argument to the first parameter of {@link #frustumPlane(int, Vector4d)}
     * identifying the plane with equation <tt>y=1</tt> when using the identity matrix.  
     */
    public static final int PLANE_PY = 3;
    /**
     * Argument to the first parameter of {@link #frustumPlane(int, Vector4d)}
     * identifying the plane with equation <tt>z=-1</tt> when using the identity matrix.  
     */
    public static final int PLANE_NZ = 4;
    /**
     * Argument to the first parameter of {@link #frustumPlane(int, Vector4d)}
     * identifying the plane with equation <tt>z=1</tt> when using the identity matrix.  
     */
    public static final int PLANE_PZ = 5;

    /**
     * Argument to the first parameter of {@link #frustumCorner(int, Vector3d)}
     * identifying the corner <tt>(-1, -1, -1)</tt> when using the identity matrix.
     */
    public static final int CORNER_NXNYNZ = 0;
    /**
     * Argument to the first parameter of {@link #frustumCorner(int, Vector3d)}
     * identifying the corner <tt>(1, -1, -1)</tt> when using the identity matrix.
     */
    public static final int CORNER_PXNYNZ = 1;
    /**
     * Argument to the first parameter of {@link #frustumCorner(int, Vector3d)}
     * identifying the corner <tt>(1, 1, -1)</tt> when using the identity matrix.
     */
    public static final int CORNER_PXPYNZ = 2;
    /**
     * Argument to the first parameter of {@link #frustumCorner(int, Vector3d)}
     * identifying the corner <tt>(-1, 1, -1)</tt> when using the identity matrix.
     */
    public static final int CORNER_NXPYNZ = 3;
    /**
     * Argument to the first parameter of {@link #frustumCorner(int, Vector3d)}
     * identifying the corner <tt>(1, -1, 1)</tt> when using the identity matrix.
     */
    public static final int CORNER_PXNYPZ = 4;
    /**
     * Argument to the first parameter of {@link #frustumCorner(int, Vector3d)}
     * identifying the corner <tt>(-1, -1, 1)</tt> when using the identity matrix.
     */
    public static final int CORNER_NXNYPZ = 5;
    /**
     * Argument to the first parameter of {@link #frustumCorner(int, Vector3d)}
     * identifying the corner <tt>(-1, 1, 1)</tt> when using the identity matrix.
     */
    public static final int CORNER_NXPYPZ = 6;
    /**
     * Argument to the first parameter of {@link #frustumCorner(int, Vector3d)}
     * identifying the corner <tt>(1, 1, 1)</tt> when using the identity matrix.
     */
    public static final int CORNER_PXPYPZ = 7;

    /**
     * The components of this matrix in column-major order.
     */
    public final double[] ms = new double[16];

    /**
     * Create a new {@link Matrix4d} and set it to {@link #identity() identity}.
     */
    public Matrix4d() {
        ms[M00] = 1.0;
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = 1.0;
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        ms[M22] = 1.0;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
    }

    /**
     * Create a new {@link Matrix4d} and make it a copy of the given matrix.
     * 
     * @param mat
     *          the {@link Matrix4d} to copy the values from
     */
    public Matrix4d(Matrix4d mat) {
        ms[M00] = mat.ms[M00];
        ms[M01] = mat.ms[M01];
        ms[M02] = mat.ms[M02];
        ms[M03] = mat.ms[M03];
        ms[M10] = mat.ms[M10];
        ms[M11] = mat.ms[M11];
        ms[M12] = mat.ms[M12];
        ms[M13] = mat.ms[M13];
        ms[M20] = mat.ms[M20];
        ms[M21] = mat.ms[M21];
        ms[M22] = mat.ms[M22];
        ms[M23] = mat.ms[M23];
        ms[M30] = mat.ms[M30];
        ms[M31] = mat.ms[M31];
        ms[M32] = mat.ms[M32];
        ms[M33] = mat.ms[M33];
    }

    /**
     * Create a new {@link Matrix4d} and make it a copy of the given matrix.
     * 
     * @param mat
     *          the {@link Matrix4f} to copy the values from
     */
    public Matrix4d(Matrix4f mat) {
        ms[M00] = mat.ms[M00];
        ms[M01] = mat.ms[M01];
        ms[M02] = mat.ms[M02];
        ms[M03] = mat.ms[M03];
        ms[M10] = mat.ms[M10];
        ms[M11] = mat.ms[M11];
        ms[M12] = mat.ms[M12];
        ms[M13] = mat.ms[M13];
        ms[M20] = mat.ms[M20];
        ms[M21] = mat.ms[M21];
        ms[M22] = mat.ms[M22];
        ms[M23] = mat.ms[M23];
        ms[M30] = mat.ms[M30];
        ms[M31] = mat.ms[M31];
        ms[M32] = mat.ms[M32];
        ms[M33] = mat.ms[M33];
    }

    /**
     * Create a new {@link Matrix4d} by setting its uppper left 3x3 submatrix to the values of the given {@link Matrix3d}
     * and the rest to identity.
     * 
     * @param mat
     *          the {@link Matrix3d}
     */
    public Matrix4d(Matrix3d mat) {
        ms[M00] = mat.ms[Matrix3d.M00];
        ms[M01] = mat.ms[Matrix3d.M01];
        ms[M02] = mat.ms[Matrix3d.M02];
        ms[M10] = mat.ms[Matrix3d.M10];
        ms[M11] = mat.ms[Matrix3d.M11];
        ms[M12] = mat.ms[Matrix3d.M12];
        ms[M20] = mat.ms[Matrix3d.M20];
        ms[M21] = mat.ms[Matrix3d.M21];
        ms[M22] = mat.ms[Matrix3d.M22];
        ms[M33] = 1.0;
    }

    /**
     * Create a new 4x4 matrix using the supplied float values.
     * 
     * @param n00
     *          the value of ms[M00]
     * @param n01
     *          the value of ms[M01]
     * @param n02
     *          the value of ms[M02]
     * @param n03
     *          the value of ms[M03]
     * @param n10
     *          the value of ms[M10]
     * @param n11
     *          the value of ms[M11]
     * @param n12
     *          the value of ms[M12]
     * @param n13
     *          the value of ms[M13]
     * @param n20
     *          the value of ms[M20]
     * @param n21
     *          the value of ms[M21]
     * @param n22
     *          the value of ms[M22]
     * @param n23
     *          the value of ms[M23]
     * @param n30
     *          the value of ms[M30]
     * @param n31
     *          the value of ms[M31]
     * @param n32
     *          the value of ms[M32]
     * @param n33
     *          the value of ms[M33]
     */
    public Matrix4d(double n00, double n01, double n02, double n03,
                    double n10, double n11, double n12, double n13,
                    double n20, double n21, double n22, double n23, 
                    double n30, double n31, double n32, double n33) {
        ms[M00] = n00;
        ms[M01] = n01;
        ms[M02] = n02;
        ms[M03] = n03;
        ms[M10] = n10;
        ms[M11] = n11;
        ms[M12] = n12;
        ms[M13] = n13;
        ms[M20] = n20;
        ms[M21] = n21;
        ms[M22] = n22;
        ms[M23] = n23;
        ms[M30] = n30;
        ms[M31] = n31;
        ms[M32] = n32;
        ms[M33] = n33;
    }

    /**
     * Return the value of the matrix element at column 0 and row 0.
     * 
     * @return the value of the matrix element
     */
    public double m00() {
        return ms[M00];
    }
    /**
     * Return the value of the matrix element at column 0 and row 1.
     * 
     * @return the value of the matrix element
     */
    public double m01() {
        return ms[M01];
    }
    /**
     * Return the value of the matrix element at column 0 and row 2.
     * 
     * @return the value of the matrix element
     */
    public double m02() {
        return ms[M02];
    }
    /**
     * Return the value of the matrix element at column 0 and row 3.
     * 
     * @return the value of the matrix element
     */
    public double m03() {
        return ms[M03];
    }
    /**
     * Return the value of the matrix element at column 1 and row 0.
     * 
     * @return the value of the matrix element
     */
    public double m10() {
        return ms[M10];
    }
    /**
     * Return the value of the matrix element at column 1 and row 1.
     * 
     * @return the value of the matrix element
     */
    public double m11() {
        return ms[M11];
    }
    /**
     * Return the value of the matrix element at column 1 and row 2.
     * 
     * @return the value of the matrix element
     */
    public double m12() {
        return ms[M12];
    }
    /**
     * Return the value of the matrix element at column 1 and row 3.
     * 
     * @return the value of the matrix element
     */
    public double m13() {
        return ms[M13];
    }
    /**
     * Return the value of the matrix element at column 2 and row 0.
     * 
     * @return the value of the matrix element
     */
    public double m20() {
        return ms[M20];
    }
    /**
     * Return the value of the matrix element at column 2 and row 1.
     * 
     * @return the value of the matrix element
     */
    public double m21() {
        return ms[M21];
    }
    /**
     * Return the value of the matrix element at column 2 and row 2.
     * 
     * @return the value of the matrix element
     */
    public double m22() {
        return ms[M22];
    }
    /**
     * Return the value of the matrix element at column 2 and row 3.
     * 
     * @return the value of the matrix element
     */
    public double m23() {
        return ms[M23];
    }
    /**
     * Return the value of the matrix element at column 3 and row 0.
     * 
     * @return the value of the matrix element
     */
    public double m30() {
        return ms[M30];
    }
    /**
     * Return the value of the matrix element at column 3 and row 1.
     * 
     * @return the value of the matrix element
     */
    public double m31() {
        return ms[M31];
    }
    /**
     * Return the value of the matrix element at column 3 and row 2.
     * 
     * @return the value of the matrix element
     */
    public double m32() {
        return ms[M32];
    }
    /**
     * Return the value of the matrix element at column 3 and row 3.
     * 
     * @return the value of the matrix element
     */
    public double m33() {
        return ms[M33];
    }

    /**
     * Set the value of the matrix element at column 0 and row 0
     * 
     * @param m00
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m00(double m00) {
        this.ms[M00] = m00;
        return this;
    }
    /**
     * Set the value of the matrix element at column 0 and row 1
     * 
     * @param m01
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m01(double m01) {
        this.ms[M01] = m01;
        return this;
    }
    /**
     * Set the value of the matrix element at column 0 and row 2
     * 
     * @param m02
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m02(double m02) {
        this.ms[M02] = m02;
        return this;
    }
    /**
     * Set the value of the matrix element at column 0 and row 3
     * 
     * @param m03
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m03(double m03) {
        this.ms[M03] = m03;
        return this;
    }
    /**
     * Set the value of the matrix element at column 1 and row 0
     * 
     * @param m10
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m10(double m10) {
        this.ms[M10] = m10;
        return this;
    }
    /**
     * Set the value of the matrix element at column 1 and row 1
     * 
     * @param m11
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m11(double m11) {
        this.ms[M11] = m11;
        return this;
    }
    /**
     * Set the value of the matrix element at column 1 and row 2
     * 
     * @param m12
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m12(double m12) {
        this.ms[M12] = m12;
        return this;
    }
    /**
     * Set the value of the matrix element at column 1 and row 3
     * 
     * @param m13
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m13(double m13) {
        this.ms[M13] = m13;
        return this;
    }
    /**
     * Set the value of the matrix element at column 2 and row 0
     * 
     * @param m20
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m20(double m20) {
        this.ms[M20] = m20;
        return this;
    }
    /**
     * Set the value of the matrix element at column 2 and row 1
     * 
     * @param m21
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m21(double m21) {
        this.ms[M21] = m21;
        return this;
    }
    /**
     * Set the value of the matrix element at column 2 and row 2
     * 
     * @param m22
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m22(double m22) {
        this.ms[M22] = m22;
        return this;
    }
    /**
     * Set the value of the matrix element at column 2 and row 3
     * 
     * @param m23
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m23(double m23) {
        this.ms[M23] = m23;
        return this;
    }
    /**
     * Set the value of the matrix element at column 3 and row 0
     * 
     * @param m30
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m30(double m30) {
        this.ms[M30] = m30;
        return this;
    }
    /**
     * Set the value of the matrix element at column 3 and row 1
     * 
     * @param m31
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m31(double m31) {
        this.ms[M31] = m31;
        return this;
    }
    /**
     * Set the value of the matrix element at column 3 and row 2
     * 
     * @param m32
     *          the new value
     * @return the value of the matrix element
     */
    public Matrix4d m32(double m32) {
        this.ms[M32] = m32;
        return this;
    }
    /**
     * Set the value of the matrix element at column 3 and row 3
     * 
     * @param m33
     *          the new value
     * @return this
     */
    public Matrix4d m33(double m33) {
        this.ms[M33] = m33;
        return this;
    }

    /**
     * Reset this matrix to the identity.
     * <p>
     * Please note that if a call to {@link #identity()} is immediately followed by a call to:
     * {@link #translate(double, double, double) translate}, 
     * {@link #rotate(double, double, double, double) rotate},
     * {@link #scale(double, double, double) scale},
     * {@link #perspective(double, double, double, double) perspective},
     * {@link #frustum(double, double, double, double, double, double) frustum},
     * {@link #ortho(double, double, double, double, double, double) ortho},
     * {@link #ortho2D(double, double, double, double) ortho2D},
     * {@link #lookAt(double, double, double, double, double, double, double, double, double) lookAt},
     * {@link #lookAlong(double, double, double, double, double, double) lookAlong},
     * or any of their overloads, then the call to {@link #identity()} can be omitted and the subsequent call replaced with:
     * {@link #translation(double, double, double) translation},
     * {@link #rotation(double, double, double, double) rotation},
     * {@link #scaling(double, double, double) scaling},
     * {@link #setPerspective(double, double, double, double) setPerspective},
     * {@link #setFrustum(double, double, double, double, double, double) setFrustum},
     * {@link #setOrtho(double, double, double, double, double, double) setOrtho},
     * {@link #setOrtho2D(double, double, double, double) setOrtho2D},
     * {@link #setLookAt(double, double, double, double, double, double, double, double, double) setLookAt},
     * {@link #setLookAlong(double, double, double, double, double, double) setLookAlong},
     * or any of their overloads.
     * 
     * @return this
     */
    public Matrix4d identity() {
        ms[M00] = 1.0;
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = 1.0;
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        ms[M22] = 1.0;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Store the values of the given matrix <code>m</code> into <code>this</code> matrix.
     * 
     * @see #Matrix4d(Matrix4d)
     * @see #get(Matrix4d)
     * 
     * @param m
     *          the matrix to copy the values from
     * @return this
     */
    public Matrix4d set(Matrix4d m) {
        ms[M00] = m.ms[M00];
        ms[M01] = m.ms[M01];
        ms[M02] = m.ms[M02];
        ms[M03] = m.ms[M03];
        ms[M10] = m.ms[M10];
        ms[M11] = m.ms[M11];
        ms[M12] = m.ms[M12];
        ms[M13] = m.ms[M13];
        ms[M20] = m.ms[M20];
        ms[M21] = m.ms[M21];
        ms[M22] = m.ms[M22];
        ms[M23] = m.ms[M23];
        ms[M30] = m.ms[M30];
        ms[M31] = m.ms[M31];
        ms[M32] = m.ms[M32];
        ms[M33] = m.ms[M33];
        return this;
    }

    /**
     * Set the upper left 3x3 submatrix of this {@link Matrix4d} to the given {@link Matrix3d} 
     * and the rest to identity.
     * 
     * @see #Matrix4d(Matrix3d)
     * 
     * @param mat
     *          the {@link Matrix3d}
     * @return this
     */
    public Matrix4d set(Matrix3d mat) {
        ms[M00] = mat.ms[Matrix3d.M00];
        ms[M01] = mat.ms[Matrix3d.M01];
        ms[M02] = mat.ms[Matrix3d.M02];
        ms[M03] = 0.0;
        ms[M10] = mat.ms[Matrix3d.M10];
        ms[M11] = mat.ms[Matrix3d.M11];
        ms[M12] = mat.ms[Matrix3d.M12];
        ms[M13] = 0.0;
        ms[M20] = mat.ms[Matrix3d.M20];
        ms[M21] = mat.ms[Matrix3d.M21];
        ms[M22] = mat.ms[Matrix3d.M22];
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Store the values of the given matrix <code>m</code> into <code>this</code> matrix.
     * 
     * @see #Matrix4d(Matrix4f)
     * 
     * @param m
     *          the matrix to copy the values from
     * @return this
     */
    public Matrix4d set(Matrix4f m) {
        ms[M00] = m.ms[M00];
        ms[M01] = m.ms[M01];
        ms[M02] = m.ms[M02];
        ms[M03] = m.ms[M03];
        ms[M10] = m.ms[M10];
        ms[M11] = m.ms[M11];
        ms[M12] = m.ms[M12];
        ms[M13] = m.ms[M13];
        ms[M20] = m.ms[M20];
        ms[M21] = m.ms[M21];
        ms[M22] = m.ms[M22];
        ms[M23] = m.ms[M23];
        ms[M30] = m.ms[M30];
        ms[M31] = m.ms[M31];
        ms[M32] = m.ms[M32];
        ms[M33] = m.ms[M33];
        return this;
    }

    /**
     * Set the upper left 3x3 submatrix of this {@link Matrix4d} to that of the given {@link Matrix4d} 
     * and the rest to identity.
     * 
     * @param mat
     *          the {@link Matrix4d}
     * @return this
     */
    public Matrix4d set3x3(Matrix4d mat) {
        ms[M00] = mat.ms[M00];
        ms[M01] = mat.ms[M01];
        ms[M02] = mat.ms[M02];
        ms[M03] = 0.0;
        ms[M10] = mat.ms[M10];
        ms[M11] = mat.ms[M11];
        ms[M12] = mat.ms[M12];
        ms[M13] = 0.0;
        ms[M20] = mat.ms[M20];
        ms[M21] = mat.ms[M21];
        ms[M22] = mat.ms[M22];
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to be equivalent to the rotation specified by the given {@link AxisAngle4f}.
     * 
     * @param axisAngle
     *          the {@link AxisAngle4f}
     * @return this
     */
    public Matrix4d set(AxisAngle4f axisAngle) {
        double x = axisAngle.x;
        double y = axisAngle.y;
        double z = axisAngle.z;
        double angle = axisAngle.angle;
        double invLength = 1.0 / Math.sqrt(x*x + y*y + z*z);
        x *= invLength;
        y *= invLength;
        z *= invLength;
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double omc = 1.0 - c;
        ms[M00] = c + x*x*omc;
        ms[M11] = c + y*y*omc;
        ms[M22] = c + z*z*omc;
        double tmp1 = x*y*omc;
        double tmp2 = z*s;
        ms[M10] = tmp1 - tmp2;
        ms[M01] = tmp1 + tmp2;
        tmp1 = x*z*omc;
        tmp2 = y*s;
        ms[M20] = tmp1 + tmp2;
        ms[M02] = tmp1 - tmp2;
        tmp1 = y*z*omc;
        tmp2 = x*s;
        ms[M21] = tmp1 - tmp2;
        ms[M12] = tmp1 + tmp2;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to be equivalent to the rotation specified by the given {@link AxisAngle4d}.
     * 
     * @param axisAngle
     *          the {@link AxisAngle4d}
     * @return this
     */
    public Matrix4d set(AxisAngle4d axisAngle) {
        double x = axisAngle.x;
        double y = axisAngle.y;
        double z = axisAngle.z;
        double angle = axisAngle.angle;
        double invLength = 1.0 / Math.sqrt(x*x + y*y + z*z);
        x *= invLength;
        y *= invLength;
        z *= invLength;
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double omc = 1.0 - c;
        ms[M00] = c + x*x*omc;
        ms[M11] = c + y*y*omc;
        ms[M22] = c + z*z*omc;
        double tmp1 = x*y*omc;
        double tmp2 = z*s;
        ms[M10] = tmp1 - tmp2;
        ms[M01] = tmp1 + tmp2;
        tmp1 = x*z*omc;
        tmp2 = y*s;
        ms[M20] = tmp1 + tmp2;
        ms[M02] = tmp1 - tmp2;
        tmp1 = y*z*omc;
        tmp2 = x*s;
        ms[M21] = tmp1 - tmp2;
        ms[M12] = tmp1 + tmp2;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to be equivalent to the rotation specified by the given {@link Quaternionf}.
     * 
     * @see Quaternionf#get(Matrix4d)
     * 
     * @param q
     *          the {@link Quaternionf}
     * @return this
     */
    public Matrix4d set(Quaternionf q) {
        return q.get(this);
    }

    /**
     * Set this matrix to be equivalent to the rotation specified by the given {@link Quaterniond}.
     * 
     * @see Quaterniond#get(Matrix4d)
     * 
     * @param q
     *          the {@link Quaterniond}
     * @return this
     */
    public Matrix4d set(Quaterniond q) {
        return q.get(this);
    }

    /**
     * Multiply this matrix by the supplied <code>right</code> matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * transformation of the right matrix will be applied first!
     * 
     * @param right
     *          the right operand of the multiplication
     * @return this
     */
    public Matrix4d mul(Matrix4d right) {
        return mul(right, this);
    }

    /**
     * Multiply this matrix by the supplied <code>right</code> matrix and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * transformation of the right matrix will be applied first!
     * 
     * @param right
     *          the right operand of the multiplication
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d mul(Matrix4d right, Matrix4d dest) {
        dest.set(ms[M00] * right.ms[M00] + ms[M10] * right.ms[M01] + ms[M20] * right.ms[M02] + ms[M30] * right.ms[M03],
                 ms[M01] * right.ms[M00] + ms[M11] * right.ms[M01] + ms[M21] * right.ms[M02] + ms[M31] * right.ms[M03],
                 ms[M02] * right.ms[M00] + ms[M12] * right.ms[M01] + ms[M22] * right.ms[M02] + ms[M32] * right.ms[M03],
                 ms[M03] * right.ms[M00] + ms[M13] * right.ms[M01] + ms[M23] * right.ms[M02] + ms[M33] * right.ms[M03],
                 ms[M00] * right.ms[M10] + ms[M10] * right.ms[M11] + ms[M20] * right.ms[M12] + ms[M30] * right.ms[M13],
                 ms[M01] * right.ms[M10] + ms[M11] * right.ms[M11] + ms[M21] * right.ms[M12] + ms[M31] * right.ms[M13],
                 ms[M02] * right.ms[M10] + ms[M12] * right.ms[M11] + ms[M22] * right.ms[M12] + ms[M32] * right.ms[M13],
                 ms[M03] * right.ms[M10] + ms[M13] * right.ms[M11] + ms[M23] * right.ms[M12] + ms[M33] * right.ms[M13],
                 ms[M00] * right.ms[M20] + ms[M10] * right.ms[M21] + ms[M20] * right.ms[M22] + ms[M30] * right.ms[M23],
                 ms[M01] * right.ms[M20] + ms[M11] * right.ms[M21] + ms[M21] * right.ms[M22] + ms[M31] * right.ms[M23],
                 ms[M02] * right.ms[M20] + ms[M12] * right.ms[M21] + ms[M22] * right.ms[M22] + ms[M32] * right.ms[M23],
                 ms[M03] * right.ms[M20] + ms[M13] * right.ms[M21] + ms[M23] * right.ms[M22] + ms[M33] * right.ms[M23],
                 ms[M00] * right.ms[M30] + ms[M10] * right.ms[M31] + ms[M20] * right.ms[M32] + ms[M30] * right.ms[M33],
                 ms[M01] * right.ms[M30] + ms[M11] * right.ms[M31] + ms[M21] * right.ms[M32] + ms[M31] * right.ms[M33],
                 ms[M02] * right.ms[M30] + ms[M12] * right.ms[M31] + ms[M22] * right.ms[M32] + ms[M32] * right.ms[M33],
                 ms[M03] * right.ms[M30] + ms[M13] * right.ms[M31] + ms[M23] * right.ms[M32] + ms[M33] * right.ms[M33]);
        return dest;
    }

    /**
     * Multiply this matrix by the supplied parameter matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * transformation of the right matrix will be applied first!
     * 
     * @param right
     *          the right operand of the multiplication
     * @return this
     */
    public Matrix4d mul(Matrix4f right) {
        return mul(right, this);
    }

    /**
     * Multiply this matrix by the supplied parameter matrix and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * transformation of the right matrix will be applied first!
     * 
     * @param right
     *          the right operand of the multiplication
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d mul(Matrix4f right, Matrix4d dest) {
        dest.set(ms[M00] * right.ms[M00] + ms[M10] * right.ms[M01] + ms[M20] * right.ms[M02] + ms[M30] * right.ms[M03],
                 ms[M01] * right.ms[M00] + ms[M11] * right.ms[M01] + ms[M21] * right.ms[M02] + ms[M31] * right.ms[M03],
                 ms[M02] * right.ms[M00] + ms[M12] * right.ms[M01] + ms[M22] * right.ms[M02] + ms[M32] * right.ms[M03],
                 ms[M03] * right.ms[M00] + ms[M13] * right.ms[M01] + ms[M23] * right.ms[M02] + ms[M33] * right.ms[M03],
                 ms[M00] * right.ms[M10] + ms[M10] * right.ms[M11] + ms[M20] * right.ms[M12] + ms[M30] * right.ms[M13],
                 ms[M01] * right.ms[M10] + ms[M11] * right.ms[M11] + ms[M21] * right.ms[M12] + ms[M31] * right.ms[M13],
                 ms[M02] * right.ms[M10] + ms[M12] * right.ms[M11] + ms[M22] * right.ms[M12] + ms[M32] * right.ms[M13],
                 ms[M03] * right.ms[M10] + ms[M13] * right.ms[M11] + ms[M23] * right.ms[M12] + ms[M33] * right.ms[M13],
                 ms[M00] * right.ms[M20] + ms[M10] * right.ms[M21] + ms[M20] * right.ms[M22] + ms[M30] * right.ms[M23],
                 ms[M01] * right.ms[M20] + ms[M11] * right.ms[M21] + ms[M21] * right.ms[M22] + ms[M31] * right.ms[M23],
                 ms[M02] * right.ms[M20] + ms[M12] * right.ms[M21] + ms[M22] * right.ms[M22] + ms[M32] * right.ms[M23],
                 ms[M03] * right.ms[M20] + ms[M13] * right.ms[M21] + ms[M23] * right.ms[M22] + ms[M33] * right.ms[M23],
                 ms[M00] * right.ms[M30] + ms[M10] * right.ms[M31] + ms[M20] * right.ms[M32] + ms[M30] * right.ms[M33],
                 ms[M01] * right.ms[M30] + ms[M11] * right.ms[M31] + ms[M21] * right.ms[M32] + ms[M31] * right.ms[M33],
                 ms[M02] * right.ms[M30] + ms[M12] * right.ms[M31] + ms[M22] * right.ms[M32] + ms[M32] * right.ms[M33],
                 ms[M03] * right.ms[M30] + ms[M13] * right.ms[M31] + ms[M23] * right.ms[M32] + ms[M33] * right.ms[M33]);
        return dest;
    }

    /**
     * Multiply the supplied <code>left</code> matrix by the <code>right</code> and store the result into <code>dest</code>.
     * <p>
     * If <code>L</code> is the <code>left</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>L * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>L * R * v</code>, the
     * transformation of the right matrix will be applied first!
     * 
     * @param left
     *          the left operand of the multiplication
     * @param right
     *          the right operand of the multiplication
     * @param dest
     *          will hold the result
     */
    public static void mul(Matrix4f left, Matrix4d right, Matrix4d dest) {
        dest.set(left.ms[M00] * right.ms[M00] + left.ms[M10] * right.ms[M01] + left.ms[M20] * right.ms[M02] + left.ms[M30] * right.ms[M03],
                 left.ms[M01] * right.ms[M00] + left.ms[M11] * right.ms[M01] + left.ms[M21] * right.ms[M02] + left.ms[M31] * right.ms[M03],
                 left.ms[M02] * right.ms[M00] + left.ms[M12] * right.ms[M01] + left.ms[M22] * right.ms[M02] + left.ms[M32] * right.ms[M03],
                 left.ms[M03] * right.ms[M00] + left.ms[M13] * right.ms[M01] + left.ms[M23] * right.ms[M02] + left.ms[M33] * right.ms[M03],
                 left.ms[M00] * right.ms[M10] + left.ms[M10] * right.ms[M11] + left.ms[M20] * right.ms[M12] + left.ms[M30] * right.ms[M13],
                 left.ms[M01] * right.ms[M10] + left.ms[M11] * right.ms[M11] + left.ms[M21] * right.ms[M12] + left.ms[M31] * right.ms[M13],
                 left.ms[M02] * right.ms[M10] + left.ms[M12] * right.ms[M11] + left.ms[M22] * right.ms[M12] + left.ms[M32] * right.ms[M13],
                 left.ms[M03] * right.ms[M10] + left.ms[M13] * right.ms[M11] + left.ms[M23] * right.ms[M12] + left.ms[M33] * right.ms[M13],
                 left.ms[M00] * right.ms[M20] + left.ms[M10] * right.ms[M21] + left.ms[M20] * right.ms[M22] + left.ms[M30] * right.ms[M23],
                 left.ms[M01] * right.ms[M20] + left.ms[M11] * right.ms[M21] + left.ms[M21] * right.ms[M22] + left.ms[M31] * right.ms[M23],
                 left.ms[M02] * right.ms[M20] + left.ms[M12] * right.ms[M21] + left.ms[M22] * right.ms[M22] + left.ms[M32] * right.ms[M23],
                 left.ms[M03] * right.ms[M20] + left.ms[M13] * right.ms[M21] + left.ms[M23] * right.ms[M22] + left.ms[M33] * right.ms[M23],
                 left.ms[M00] * right.ms[M30] + left.ms[M10] * right.ms[M31] + left.ms[M20] * right.ms[M32] + left.ms[M30] * right.ms[M33],
                 left.ms[M01] * right.ms[M30] + left.ms[M11] * right.ms[M31] + left.ms[M21] * right.ms[M32] + left.ms[M31] * right.ms[M33],
                 left.ms[M02] * right.ms[M30] + left.ms[M12] * right.ms[M31] + left.ms[M22] * right.ms[M32] + left.ms[M32] * right.ms[M33],
                 left.ms[M03] * right.ms[M30] + left.ms[M13] * right.ms[M31] + left.ms[M23] * right.ms[M32] + left.ms[M33] * right.ms[M33]);
    }

    /**
     * Multiply <code>this</code> symmetric perspective projection matrix by the supplied {@link #isAffine() affine} <code>view</code> matrix.
     * <p>
     * If <code>P</code> is <code>this</code> matrix and <code>V</code> the <code>view</code> matrix,
     * then the new matrix will be <code>P * V</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>P * V * v</code>, the
     * transformation of the <code>view</code> matrix will be applied first!
     *
     * @param view
     *          the {@link #isAffine() affine} matrix to multiply <code>this</code> symmetric perspective projection matrix by
     * @return dest
     */
    public Matrix4d mulPerspectiveAffine(Matrix4d view) {
       return mulPerspectiveAffine(view, this);
    }

    /**
     * Multiply <code>this</code> symmetric perspective projection matrix by the supplied {@link #isAffine() affine} <code>view</code> matrix and store the result in <code>dest</code>.
     * <p>
     * If <code>P</code> is <code>this</code> matrix and <code>V</code> the <code>view</code> matrix,
     * then the new matrix will be <code>P * V</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>P * V * v</code>, the
     * transformation of the <code>view</code> matrix will be applied first!
     *
     * @param view
     *          the {@link #isAffine() affine} matrix to multiply <code>this</code> symmetric perspective projection matrix by
     * @param dest
     *          the destination matrix, which will hold the result
     * @return dest
     */
    public Matrix4d mulPerspectiveAffine(Matrix4d view, Matrix4d dest) {
        dest.set(ms[M00] * view.ms[M00], ms[M11] * view.ms[M01], ms[M22] * view.ms[M02], ms[M23] * view.ms[M02],
                 ms[M00] * view.ms[M10], ms[M11] * view.ms[M11], ms[M22] * view.ms[M12], ms[M23] * view.ms[M12],
                 ms[M00] * view.ms[M20], ms[M11] * view.ms[M21], ms[M22] * view.ms[M22], ms[M23] * view.ms[M22],
                 ms[M00] * view.ms[M30], ms[M11] * view.ms[M31], ms[M22] * view.ms[M32] + ms[M32], ms[M23] * view.ms[M32]);
        return dest;
    }

    /**
     * Multiply this matrix by the supplied <code>right</code> matrix, which is assumed to be {@link #isAffine() affine}, and store the result in <code>this</code>.
     * <p>
     * This method assumes that the given <code>right</code> matrix represents an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * transformation of the right matrix will be applied first!
     *
     * @param right
     *          the right operand of the matrix multiplication (the last row is assumed to be <tt>(0, 0, 0, 1)</tt>)
     * @return this
     */
    public Matrix4d mulAffineR(Matrix4d right) {
       return mulAffineR(right, this);
    }

    /**
     * Multiply this matrix by the supplied <code>right</code> matrix, which is assumed to be {@link #isAffine() affine}, and store the result in <code>dest</code>.
     * <p>
     * This method assumes that the given <code>right</code> matrix represents an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * transformation of the right matrix will be applied first!
     *
     * @param right
     *          the right operand of the matrix multiplication (the last row is assumed to be <tt>(0, 0, 0, 1)</tt>)
     * @param dest
     *          the destination matrix, which will hold the result
     * @return dest
     */
    public Matrix4d mulAffineR(Matrix4d right, Matrix4d dest) {
        dest.set(ms[M00] * right.ms[M00] + ms[M10] * right.ms[M01] + ms[M20] * right.ms[M02],
                 ms[M01] * right.ms[M00] + ms[M11] * right.ms[M01] + ms[M21] * right.ms[M02],
                 ms[M02] * right.ms[M00] + ms[M12] * right.ms[M01] + ms[M22] * right.ms[M02],
                 ms[M03] * right.ms[M00] + ms[M13] * right.ms[M01] + ms[M23] * right.ms[M02],
                 ms[M00] * right.ms[M10] + ms[M10] * right.ms[M11] + ms[M20] * right.ms[M12],
                 ms[M01] * right.ms[M10] + ms[M11] * right.ms[M11] + ms[M21] * right.ms[M12],
                 ms[M02] * right.ms[M10] + ms[M12] * right.ms[M11] + ms[M22] * right.ms[M12],
                 ms[M03] * right.ms[M10] + ms[M13] * right.ms[M11] + ms[M23] * right.ms[M12],
                 ms[M00] * right.ms[M20] + ms[M10] * right.ms[M21] + ms[M20] * right.ms[M22],
                 ms[M01] * right.ms[M20] + ms[M11] * right.ms[M21] + ms[M21] * right.ms[M22],
                 ms[M02] * right.ms[M20] + ms[M12] * right.ms[M21] + ms[M22] * right.ms[M22],
                 ms[M03] * right.ms[M20] + ms[M13] * right.ms[M21] + ms[M23] * right.ms[M22],
                 ms[M00] * right.ms[M30] + ms[M10] * right.ms[M31] + ms[M20] * right.ms[M32] + ms[M30],
                 ms[M01] * right.ms[M30] + ms[M11] * right.ms[M31] + ms[M21] * right.ms[M32] + ms[M31],
                 ms[M02] * right.ms[M30] + ms[M12] * right.ms[M31] + ms[M22] * right.ms[M32] + ms[M32],
                 ms[M03] * right.ms[M30] + ms[M13] * right.ms[M31] + ms[M23] * right.ms[M32] + ms[M33]);
        return dest;
    }

    /**
     * Multiply this matrix by the supplied <code>right</code> matrix, both of which are assumed to be {@link #isAffine() affine}, and store the result in <code>this</code>.
     * <p>
     * This method assumes that <code>this</code> matrix and the given <code>right</code> matrix both represent an {@link #isAffine() affine} transformation
     * (i.e. their last rows are equal to <tt>(0, 0, 0, 1)</tt>)
     * and can be used to speed up matrix multiplication if the matrices only represent affine transformations, such as translation, rotation, scaling and shearing (in any combination).
     * <p>
     * This method will not modify either the last row of <code>this</code> or the last row of <code>right</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * transformation of the right matrix will be applied first!
     *
     * @param right
     *          the right operand of the matrix multiplication (the last row is assumed to be <tt>(0, 0, 0, 1)</tt>)
     * @return this
     */
    public Matrix4d mulAffine(Matrix4d right) {
       return mulAffine(right, this);
    }

    /**
     * Multiply this matrix by the supplied <code>right</code> matrix, both of which are assumed to be {@link #isAffine() affine}, and store the result in <code>dest</code>.
     * <p>
     * This method assumes that <code>this</code> matrix and the given <code>right</code> matrix both represent an {@link #isAffine() affine} transformation
     * (i.e. their last rows are equal to <tt>(0, 0, 0, 1)</tt>)
     * and can be used to speed up matrix multiplication if the matrices only represent affine transformations, such as translation, rotation, scaling and shearing (in any combination).
     * <p>
     * This method will not modify either the last row of <code>this</code> or the last row of <code>right</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the <code>right</code> matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * transformation of the right matrix will be applied first!
     *
     * @param right
     *          the right operand of the matrix multiplication (the last row is assumed to be <tt>(0, 0, 0, 1)</tt>)
     * @param dest
     *          the destination matrix, which will hold the result
     * @return dest
     */
    public Matrix4d mulAffine(Matrix4d right, Matrix4d dest) {
        dest.set(ms[M00] * right.ms[M00] + ms[M10] * right.ms[M01] + ms[M20] * right.ms[M02],
                 ms[M01] * right.ms[M00] + ms[M11] * right.ms[M01] + ms[M21] * right.ms[M02],
                 ms[M02] * right.ms[M00] + ms[M12] * right.ms[M01] + ms[M22] * right.ms[M02],
                 ms[M03],
                 ms[M00] * right.ms[M10] + ms[M10] * right.ms[M11] + ms[M20] * right.ms[M12],
                 ms[M01] * right.ms[M10] + ms[M11] * right.ms[M11] + ms[M21] * right.ms[M12],
                 ms[M02] * right.ms[M10] + ms[M12] * right.ms[M11] + ms[M22] * right.ms[M12],
                 ms[M13],
                 ms[M00] * right.ms[M20] + ms[M10] * right.ms[M21] + ms[M20] * right.ms[M22],
                 ms[M01] * right.ms[M20] + ms[M11] * right.ms[M21] + ms[M21] * right.ms[M22],
                 ms[M02] * right.ms[M20] + ms[M12] * right.ms[M21] + ms[M22] * right.ms[M22],
                 ms[M23],
                 ms[M00] * right.ms[M30] + ms[M10] * right.ms[M31] + ms[M20] * right.ms[M32] + ms[M30],
                 ms[M01] * right.ms[M30] + ms[M11] * right.ms[M31] + ms[M21] * right.ms[M32] + ms[M31],
                 ms[M02] * right.ms[M30] + ms[M12] * right.ms[M31] + ms[M22] * right.ms[M32] + ms[M32],
                 ms[M33]);
        return dest;
    }

    /**
     * Multiply <code>this</code> orthographic projection matrix by the supplied {@link #isAffine() affine} <code>view</code> matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>V</code> the <code>view</code> matrix,
     * then the new matrix will be <code>M * V</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * V * v</code>, the
     * transformation of the <code>view</code> matrix will be applied first!
     *
     * @param view
     *          the affine matrix which to multiply <code>this</code> with
     * @return dest
     */
    public Matrix4d mulOrthoAffine(Matrix4d view) {
        return mulOrthoAffine(view, this);
    }

    /**
     * Multiply <code>this</code> orthographic projection matrix by the supplied {@link #isAffine() affine} <code>view</code> matrix
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>V</code> the <code>view</code> matrix,
     * then the new matrix will be <code>M * V</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * V * v</code>, the
     * transformation of the <code>view</code> matrix will be applied first!
     *
     * @param view
     *          the affine matrix which to multiply <code>this</code> with
     * @param dest
     *          the destination matrix, which will hold the result
     * @return dest
     */
    public Matrix4d mulOrthoAffine(Matrix4d view, Matrix4d dest) {
        dest.set(ms[M00] * view.ms[M00], ms[M11] * view.ms[M01], ms[M22] * view.ms[M02], 0.0,
                 ms[M00] * view.ms[M10], ms[M11] * view.ms[M11], ms[M22] * view.ms[M12], 0.0,
                 ms[M00] * view.ms[M20], ms[M11] * view.ms[M21], ms[M22] * view.ms[M22], 0.0,
                 ms[M00] * view.ms[M30] + ms[M30], ms[M11] * view.ms[M31] + ms[M31], ms[M22] * view.ms[M32] + ms[M32], 1.0);
        return dest;
    }

    /**
     * Component-wise add the upper 4x3 submatrices of <code>this</code> and <code>other</code>
     * by first multiplying each component of <code>other</code>'s 4x3 submatrix by <code>otherFactor</code> and
     * adding that result to <code>this</code>.
     * <p>
     * The matrix <code>other</code> will not be changed.
     * 
     * @param other
     *          the other matrix
     * @param otherFactor
     *          the factor to multiply each of the other matrix's 4x3 components
     * @return this
     */
    public Matrix4d fma4x3(Matrix4d other, double otherFactor) {
        return fma4x3(other, otherFactor, this);
    }

    /**
     * Component-wise add the upper 4x3 submatrices of <code>this</code> and <code>other</code>
     * by first multiplying each component of <code>other</code>'s 4x3 submatrix by <code>otherFactor</code>,
     * adding that to <code>this</code> and storing the final result in <code>dest</code>.
     * <p>
     * The other components of <code>dest</code> will be set to the ones of <code>this</code>.
     * <p>
     * The matrices <code>this</code> and <code>other</code> will not be changed.
     * 
     * @param other
     *          the other matrix
     * @param otherFactor
     *          the factor to multiply each of the other matrix's 4x3 components
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d fma4x3(Matrix4d other, double otherFactor, Matrix4d dest) {
        dest.ms[M00] = ms[M00] + other.ms[M00] * otherFactor;
        dest.ms[M01] = ms[M01] + other.ms[M01] * otherFactor;
        dest.ms[M02] = ms[M02] + other.ms[M02] * otherFactor;
        dest.ms[M03] = ms[M03];
        dest.ms[M10] = ms[M10] + other.ms[M10] * otherFactor;
        dest.ms[M11] = ms[M11] + other.ms[M11] * otherFactor;
        dest.ms[M12] = ms[M12] + other.ms[M12] * otherFactor;
        dest.ms[M13] = ms[M13];
        dest.ms[M20] = ms[M20] + other.ms[M20] * otherFactor;
        dest.ms[M21] = ms[M21] + other.ms[M21] * otherFactor;
        dest.ms[M22] = ms[M22] + other.ms[M22] * otherFactor;
        dest.ms[M23] = ms[M23];
        dest.ms[M30] = ms[M30] + other.ms[M30] * otherFactor;
        dest.ms[M31] = ms[M31] + other.ms[M31] * otherFactor;
        dest.ms[M32] = ms[M32] + other.ms[M32] * otherFactor;
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Component-wise add <code>this</code> and <code>other</code>.
     * 
     * @param other
     *          the other addend
     * @return this
     */
    public Matrix4d add(Matrix4d other) {
        return add(other, this);
    }

    /**
     * Component-wise add <code>this</code> and <code>other</code> and store the result in <code>dest</code>.
     * 
     * @param other
     *          the other addend
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d add(Matrix4d other, Matrix4d dest) {
        dest.ms[M00] = ms[M00] + other.ms[M00];
        dest.ms[M01] = ms[M01] + other.ms[M01];
        dest.ms[M02] = ms[M02] + other.ms[M02];
        dest.ms[M03] = ms[M03] + other.ms[M03];
        dest.ms[M10] = ms[M10] + other.ms[M10];
        dest.ms[M11] = ms[M11] + other.ms[M11];
        dest.ms[M12] = ms[M12] + other.ms[M12];
        dest.ms[M13] = ms[M13] + other.ms[M13];
        dest.ms[M20] = ms[M20] + other.ms[M20];
        dest.ms[M21] = ms[M21] + other.ms[M21];
        dest.ms[M22] = ms[M22] + other.ms[M22];
        dest.ms[M23] = ms[M23] + other.ms[M23];
        dest.ms[M30] = ms[M30] + other.ms[M30];
        dest.ms[M31] = ms[M31] + other.ms[M31];
        dest.ms[M32] = ms[M32] + other.ms[M32];
        dest.ms[M33] = ms[M33] + other.ms[M33];
        return dest;
    }

    /**
     * Component-wise subtract <code>subtrahend</code> from <code>this</code>.
     * 
     * @param subtrahend
     *          the subtrahend
     * @return this
     */
    public Matrix4d sub(Matrix4d subtrahend) {
        return sub(subtrahend, this);
    }

    /**
     * Component-wise subtract <code>subtrahend</code> from <code>this</code> and store the result in <code>dest</code>.
     * 
     * @param subtrahend
     *          the subtrahend
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d sub(Matrix4d subtrahend, Matrix4d dest) {
        dest.ms[M00] = ms[M00] - subtrahend.ms[M00];
        dest.ms[M01] = ms[M01] - subtrahend.ms[M01];
        dest.ms[M02] = ms[M02] - subtrahend.ms[M02];
        dest.ms[M03] = ms[M03] - subtrahend.ms[M03];
        dest.ms[M10] = ms[M10] - subtrahend.ms[M10];
        dest.ms[M11] = ms[M11] - subtrahend.ms[M11];
        dest.ms[M12] = ms[M12] - subtrahend.ms[M12];
        dest.ms[M13] = ms[M13] - subtrahend.ms[M13];
        dest.ms[M20] = ms[M20] - subtrahend.ms[M20];
        dest.ms[M21] = ms[M21] - subtrahend.ms[M21];
        dest.ms[M22] = ms[M22] - subtrahend.ms[M22];
        dest.ms[M23] = ms[M23] - subtrahend.ms[M23];
        dest.ms[M30] = ms[M30] - subtrahend.ms[M30];
        dest.ms[M31] = ms[M31] - subtrahend.ms[M31];
        dest.ms[M32] = ms[M32] - subtrahend.ms[M32];
        dest.ms[M33] = ms[M33] - subtrahend.ms[M33];
        return dest;
    }

    /**
     * Component-wise multiply <code>this</code> by <code>other</code>.
     * 
     * @param other
     *          the other matrix
     * @return this
     */
    public Matrix4d mulComponentWise(Matrix4d other) {
        return mulComponentWise(other, this);
    }

    /**
     * Component-wise multiply <code>this</code> by <code>other</code> and store the result in <code>dest</code>.
     * 
     * @param other
     *          the other matrix
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d mulComponentWise(Matrix4d other, Matrix4d dest) {
        dest.ms[M00] = ms[M00] * other.ms[M00];
        dest.ms[M01] = ms[M01] * other.ms[M01];
        dest.ms[M02] = ms[M02] * other.ms[M02];
        dest.ms[M03] = ms[M03] * other.ms[M03];
        dest.ms[M10] = ms[M10] * other.ms[M10];
        dest.ms[M11] = ms[M11] * other.ms[M11];
        dest.ms[M12] = ms[M12] * other.ms[M12];
        dest.ms[M13] = ms[M13] * other.ms[M13];
        dest.ms[M20] = ms[M20] * other.ms[M20];
        dest.ms[M21] = ms[M21] * other.ms[M21];
        dest.ms[M22] = ms[M22] * other.ms[M22];
        dest.ms[M23] = ms[M23] * other.ms[M23];
        dest.ms[M30] = ms[M30] * other.ms[M30];
        dest.ms[M31] = ms[M31] * other.ms[M31];
        dest.ms[M32] = ms[M32] * other.ms[M32];
        dest.ms[M33] = ms[M33] * other.ms[M33];
        return dest;
    }

    /**
     * Component-wise add the upper 4x3 submatrices of <code>this</code> and <code>other</code>.
     * 
     * @param other
     *          the other addend
     * @return this
     */
    public Matrix4d add4x3(Matrix4d other) {
        return add4x3(other, this);
    }

    /**
     * Component-wise add the upper 4x3 submatrices of <code>this</code> and <code>other</code>
     * and store the result in <code>dest</code>.
     * <p>
     * The other components of <code>dest</code> will be set to the ones of <code>this</code>.
     * 
     * @param other
     *          the other addend
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d add4x3(Matrix4d other, Matrix4d dest) {
        dest.ms[M00] = ms[M00] + other.ms[M00];
        dest.ms[M01] = ms[M01] + other.ms[M01];
        dest.ms[M02] = ms[M02] + other.ms[M02];
        dest.ms[M03] = ms[M03];
        dest.ms[M10] = ms[M10] + other.ms[M10];
        dest.ms[M11] = ms[M11] + other.ms[M11];
        dest.ms[M12] = ms[M12] + other.ms[M12];
        dest.ms[M13] = ms[M13];
        dest.ms[M20] = ms[M20] + other.ms[M20];
        dest.ms[M21] = ms[M21] + other.ms[M21];
        dest.ms[M22] = ms[M22] + other.ms[M22];
        dest.ms[M23] = ms[M23];
        dest.ms[M30] = ms[M30] + other.ms[M30];
        dest.ms[M31] = ms[M31] + other.ms[M31];
        dest.ms[M32] = ms[M32] + other.ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Component-wise subtract the upper 4x3 submatrices of <code>subtrahend</code> from <code>this</code>.
     * 
     * @param subtrahend
     *          the subtrahend
     * @return this
     */
    public Matrix4d sub4x3(Matrix4d subtrahend) {
        return sub4x3(subtrahend, this);
    }

    /**
     * Component-wise subtract the upper 4x3 submatrices of <code>subtrahend</code> from <code>this</code>
     * and store the result in <code>dest</code>.
     * <p>
     * The other components of <code>dest</code> will be set to the ones of <code>this</code>.
     * 
     * @param subtrahend
     *          the subtrahend
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d sub4x3(Matrix4d subtrahend, Matrix4d dest) {
        dest.ms[M00] = ms[M00] - subtrahend.ms[M00];
        dest.ms[M01] = ms[M01] - subtrahend.ms[M01];
        dest.ms[M02] = ms[M02] - subtrahend.ms[M02];
        dest.ms[M03] = ms[M03];
        dest.ms[M10] = ms[M10] - subtrahend.ms[M10];
        dest.ms[M11] = ms[M11] - subtrahend.ms[M11];
        dest.ms[M12] = ms[M12] - subtrahend.ms[M12];
        dest.ms[M13] = ms[M13];
        dest.ms[M20] = ms[M20] - subtrahend.ms[M20];
        dest.ms[M21] = ms[M21] - subtrahend.ms[M21];
        dest.ms[M22] = ms[M22] - subtrahend.ms[M22];
        dest.ms[M23] = ms[M23];
        dest.ms[M30] = ms[M30] - subtrahend.ms[M30];
        dest.ms[M31] = ms[M31] - subtrahend.ms[M31];
        dest.ms[M32] = ms[M32] - subtrahend.ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Component-wise multiply the upper 4x3 submatrices of <code>this</code> by <code>other</code>.
     * 
     * @param other
     *          the other matrix
     * @return this
     */
    public Matrix4d mul4x3ComponentWise(Matrix4d other) {
        return mul4x3ComponentWise(other, this);
    }

    /**
     * Component-wise multiply the upper 4x3 submatrices of <code>this</code> by <code>other</code>
     * and store the result in <code>dest</code>.
     * <p>
     * The other components of <code>dest</code> will be set to the ones of <code>this</code>.
     * 
     * @param other
     *          the other matrix
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d mul4x3ComponentWise(Matrix4d other, Matrix4d dest) {
        dest.ms[M00] = ms[M00] * other.ms[M00];
        dest.ms[M01] = ms[M01] * other.ms[M01];
        dest.ms[M02] = ms[M02] * other.ms[M02];
        dest.ms[M03] = ms[M03];
        dest.ms[M10] = ms[M10] * other.ms[M10];
        dest.ms[M11] = ms[M11] * other.ms[M11];
        dest.ms[M12] = ms[M12] * other.ms[M12];
        dest.ms[M13] = ms[M13];
        dest.ms[M20] = ms[M20] * other.ms[M20];
        dest.ms[M21] = ms[M21] * other.ms[M21];
        dest.ms[M22] = ms[M22] * other.ms[M22];
        dest.ms[M23] = ms[M23];
        dest.ms[M30] = ms[M30] * other.ms[M30];
        dest.ms[M31] = ms[M31] * other.ms[M31];
        dest.ms[M32] = ms[M32] * other.ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Set the values within this matrix to the supplied float values. The matrix will look like this:<br><br>
     *
     *  m00, m10, m20, m30<br>
     *  m01, m11, m21, m31<br>
     *  m02, m12, m22, m32<br>
     *  m03, m13, m23, m33
     * 
     * @param n00
     *          the new value of ms[M00]
     * @param n01
     *          the new value of ms[M01]
     * @param n02
     *          the new value of ms[M02]
     * @param n03
     *          the new value of ms[M03]
     * @param n10
     *          the new value of ms[M10]
     * @param n11
     *          the new value of ms[M11]
     * @param n12
     *          the new value of ms[M12]
     * @param n13
     *          the new value of ms[M13]
     * @param n20
     *          the new value of ms[M20]
     * @param n21
     *          the new value of ms[M21]
     * @param n22
     *          the new value of ms[M22]
     * @param n23
     *          the new value of ms[M23]
     * @param n30
     *          the new value of ms[M30]
     * @param n31
     *          the new value of ms[M31]
     * @param n32
     *          the new value of ms[M32]
     * @param n33
     *          the new value of ms[M33]
     * @return this
     */
    public Matrix4d set(double n00, double n01, double n02, double n03,
                        double n10, double n11, double n12, double n13,
                        double n20, double n21, double n22, double n23, 
                        double n30, double n31, double n32, double n33) {
        this.ms[M00] = n00;
        this.ms[M01] = n01;
        this.ms[M02] = n02;
        this.ms[M03] = n03;
        this.ms[M10] = n10;
        this.ms[M11] = n11;
        this.ms[M12] = n12;
        this.ms[M13] = n13;
        this.ms[M20] = n20;
        this.ms[M21] = n21;
        this.ms[M22] = n22;
        this.ms[M23] = n23;
        this.ms[M30] = n30;
        this.ms[M31] = n31;
        this.ms[M32] = n32;
        this.ms[M33] = n33;
        return this;
    }

    /**
     * Set the values in the matrix using a double array that contains the matrix elements in column-major order.
     * <p>
     * The results will look like this:<br><br>
     * 
     * 0, 4, 8, 12<br>
     * 1, 5, 9, 13<br>
     * 2, 6, 10, 14<br>
     * 3, 7, 11, 15<br>
     * 
     * @see #set(double[])
     * 
     * @param m
     *          the array to read the matrix values from
     * @param off
     *          the offset into the array
     * @return this
     */
    public Matrix4d set(double m[], int off) {
        ms[M00] = m[off+0];
        ms[M01] = m[off+1];
        ms[M02] = m[off+2];
        ms[M03] = m[off+3];
        ms[M10] = m[off+4];
        ms[M11] = m[off+5];
        ms[M12] = m[off+6];
        ms[M13] = m[off+7];
        ms[M20] = m[off+8];
        ms[M21] = m[off+9];
        ms[M22] = m[off+10];
        ms[M23] = m[off+11];
        ms[M30] = m[off+12];
        ms[M31] = m[off+13];
        ms[M32] = m[off+14];
        ms[M33] = m[off+15];
        return this;
    }

    /**
     * Set the values in the matrix using a double array that contains the matrix elements in column-major order.
     * <p>
     * The results will look like this:<br><br>
     * 
     * 0, 4, 8, 12<br>
     * 1, 5, 9, 13<br>
     * 2, 6, 10, 14<br>
     * 3, 7, 11, 15<br>
     * 
     * @see #set(double[], int)
     * 
     * @param m
     *          the array to read the matrix values from
     * @return this
     */
    public Matrix4d set(double m[]) {
        return set(m, 0);
    }

    /**
     * Set the values in the matrix using a float array that contains the matrix elements in column-major order.
     * <p>
     * The results will look like this:<br><br>
     * 
     * 0, 4, 8, 12<br>
     * 1, 5, 9, 13<br>
     * 2, 6, 10, 14<br>
     * 3, 7, 11, 15<br>
     * 
     * @see #set(float[])
     * 
     * @param m
     *          the array to read the matrix values from
     * @param off
     *          the offset into the array
     * @return this
     */
    public Matrix4d set(float m[], int off) {
        ms[M00] = m[off+0];
        ms[M01] = m[off+1];
        ms[M02] = m[off+2];
        ms[M03] = m[off+3];
        ms[M10] = m[off+4];
        ms[M11] = m[off+5];
        ms[M12] = m[off+6];
        ms[M13] = m[off+7];
        ms[M20] = m[off+8];
        ms[M21] = m[off+9];
        ms[M22] = m[off+10];
        ms[M23] = m[off+11];
        ms[M30] = m[off+12];
        ms[M31] = m[off+13];
        ms[M32] = m[off+14];
        ms[M33] = m[off+15];
        return this;
    }

    /**
     * Set the values in the matrix using a float array that contains the matrix elements in column-major order.
     * <p>
     * The results will look like this:<br><br>
     * 
     * 0, 4, 8, 12<br>
     * 1, 5, 9, 13<br>
     * 2, 6, 10, 14<br>
     * 3, 7, 11, 15<br>
     * 
     * @see #set(float[], int)
     * 
     * @param m
     *          the array to read the matrix values from
     * @return this
     */
    public Matrix4d set(float m[]) {
        return set(m, 0);
    }

    /**
     * Set the values of this matrix by reading 16 double values from the given {@link DoubleBuffer} in column-major order,
     * starting at its current position.
     * <p>
     * The DoubleBuffer is expected to contain the values in column-major order.
     * <p>
     * The position of the DoubleBuffer will not be changed by this method.
     * 
     * @param buffer
     *              the DoubleBuffer to read the matrix values from in column-major order
     * @return this
     */
    public Matrix4d set(DoubleBuffer buffer) {
        int pos = buffer.position();
        ms[M00] = buffer.get(pos);
        ms[M01] = buffer.get(pos+1);
        ms[M02] = buffer.get(pos+2);
        ms[M03] = buffer.get(pos+3);
        ms[M10] = buffer.get(pos+4);
        ms[M11] = buffer.get(pos+5);
        ms[M12] = buffer.get(pos+6);
        ms[M13] = buffer.get(pos+7);
        ms[M20] = buffer.get(pos+8);
        ms[M21] = buffer.get(pos+9);
        ms[M22] = buffer.get(pos+10);
        ms[M23] = buffer.get(pos+11);
        ms[M30] = buffer.get(pos+12);
        ms[M31] = buffer.get(pos+13);
        ms[M32] = buffer.get(pos+14);
        ms[M33] = buffer.get(pos+15);
        return this;
    }

    /**
     * Set the values of this matrix by reading 16 float values from the given {@link FloatBuffer} in column-major order,
     * starting at its current position.
     * <p>
     * The FloatBuffer is expected to contain the values in column-major order.
     * <p>
     * The position of the FloatBuffer will not be changed by this method.
     * 
     * @param buffer
     *              the FloatBuffer to read the matrix values from in column-major order
     * @return this
     */
    public Matrix4d set(FloatBuffer buffer) {
        int pos = buffer.position();
        ms[M00] = buffer.get(pos);
        ms[M01] = buffer.get(pos+1);
        ms[M02] = buffer.get(pos+2);
        ms[M03] = buffer.get(pos+3);
        ms[M10] = buffer.get(pos+4);
        ms[M11] = buffer.get(pos+5);
        ms[M12] = buffer.get(pos+6);
        ms[M13] = buffer.get(pos+7);
        ms[M20] = buffer.get(pos+8);
        ms[M21] = buffer.get(pos+9);
        ms[M22] = buffer.get(pos+10);
        ms[M23] = buffer.get(pos+11);
        ms[M30] = buffer.get(pos+12);
        ms[M31] = buffer.get(pos+13);
        ms[M32] = buffer.get(pos+14);
        ms[M33] = buffer.get(pos+15);
        return this;
    }

    /**
     * Set the values of this matrix by reading 16 double values from the given {@link ByteBuffer} in column-major order,
     * starting at its current position.
     * <p>
     * The ByteBuffer is expected to contain the values in column-major order.
     * <p>
     * The position of the ByteBuffer will not be changed by this method.
     * 
     * @param buffer
     *              the ByteBuffer to read the matrix values from in column-major order
     * @return this
     */
    public Matrix4d set(ByteBuffer buffer) {
        int pos = buffer.position();
        ms[M00] = buffer.getDouble(pos);
        ms[M01] = buffer.getDouble(pos+8*1);
        ms[M02] = buffer.getDouble(pos+8*2);
        ms[M03] = buffer.getDouble(pos+8*3);
        ms[M10] = buffer.getDouble(pos+8*4);
        ms[M11] = buffer.getDouble(pos+8*5);
        ms[M12] = buffer.getDouble(pos+8*6);
        ms[M13] = buffer.getDouble(pos+8*7);
        ms[M20] = buffer.getDouble(pos+8*8);
        ms[M21] = buffer.getDouble(pos+8*9);
        ms[M22] = buffer.getDouble(pos+8*10);
        ms[M23] = buffer.getDouble(pos+8*11);
        ms[M30] = buffer.getDouble(pos+8*12);
        ms[M31] = buffer.getDouble(pos+8*13);
        ms[M32] = buffer.getDouble(pos+8*14);
        ms[M33] = buffer.getDouble(pos+8*15);
        return this;
    }

    /**
     * Set the values of this matrix by reading 16 float values from the given {@link ByteBuffer} in column-major order,
     * starting at its current position.
     * <p>
     * The ByteBuffer is expected to contain the values in column-major order.
     * <p>
     * The position of the ByteBuffer will not be changed by this method.
     * 
     * @param buffer
     *              the ByteBuffer to read the matrix values from in column-major order
     * @return this
     */
    public Matrix4d setFloats(ByteBuffer buffer) {
        int pos = buffer.position();
        ms[M00] = buffer.getFloat(pos);
        ms[M01] = buffer.getFloat(pos+4*1);
        ms[M02] = buffer.getFloat(pos+4*2);
        ms[M03] = buffer.getFloat(pos+4*3);
        ms[M10] = buffer.getFloat(pos+4*4);
        ms[M11] = buffer.getFloat(pos+4*5);
        ms[M12] = buffer.getFloat(pos+4*6);
        ms[M13] = buffer.getFloat(pos+4*7);
        ms[M20] = buffer.getFloat(pos+4*8);
        ms[M21] = buffer.getFloat(pos+4*9);
        ms[M22] = buffer.getFloat(pos+4*10);
        ms[M23] = buffer.getFloat(pos+4*11);
        ms[M30] = buffer.getFloat(pos+4*12);
        ms[M31] = buffer.getFloat(pos+4*13);
        ms[M32] = buffer.getFloat(pos+4*14);
        ms[M33] = buffer.getFloat(pos+4*15);
        return this;
    }

    /**
     * Return the determinant of this matrix.
     * <p>
     * If <code>this</code> matrix represents an {@link #isAffine() affine} transformation, such as translation, rotation, scaling and shearing,
     * and thus its last row is equal to <tt>(0, 0, 0, 1)</tt>, then {@link #determinantAffine()} can be used instead of this method.
     * 
     * @see #determinantAffine()
     * 
     * @return the determinant
     */
    public double determinant() {
        return (ms[M00] * ms[M11] - ms[M01] * ms[M10]) * (ms[M22] * ms[M33] - ms[M23] * ms[M32])
             + (ms[M02] * ms[M10] - ms[M00] * ms[M12]) * (ms[M21] * ms[M33] - ms[M23] * ms[M31])
             + (ms[M00] * ms[M13] - ms[M03] * ms[M10]) * (ms[M21] * ms[M32] - ms[M22] * ms[M31]) 
             + (ms[M01] * ms[M12] - ms[M02] * ms[M11]) * (ms[M20] * ms[M33] - ms[M23] * ms[M30])
             + (ms[M03] * ms[M11] - ms[M01] * ms[M13]) * (ms[M20] * ms[M32] - ms[M22] * ms[M30]) 
             + (ms[M02] * ms[M13] - ms[M03] * ms[M12]) * (ms[M20] * ms[M31] - ms[M21] * ms[M30]);
    }

    /**
     * Return the determinant of the upper left 3x3 submatrix of this matrix.
     * 
     * @return the determinant
     */
    public double determinant3x3() {
        return (ms[M00] * ms[M11] - ms[M01] * ms[M10]) * ms[M22]
             + (ms[M02] * ms[M10] - ms[M00] * ms[M12]) * ms[M21]
             + (ms[M01] * ms[M12] - ms[M02] * ms[M11]) * ms[M20];
    }

    /**
     * Return the determinant of this matrix by assuming that it represents an {@link #isAffine() affine} transformation and thus
     * its last row is equal to <tt>(0, 0, 0, 1)</tt>.
     * 
     * @return the determinant
     */
    public double determinantAffine() {
        return (ms[M00] * ms[M11] - ms[M01] * ms[M10]) * ms[M22]
             + (ms[M02] * ms[M10] - ms[M00] * ms[M12]) * ms[M21]
             + (ms[M01] * ms[M12] - ms[M02] * ms[M11]) * ms[M20];
    }

    /**
     * Invert this matrix.
     * <p>
     * If <code>this</code> matrix represents an {@link #isAffine() affine} transformation, such as translation, rotation, scaling and shearing,
     * and thus its last row is equal to <tt>(0, 0, 0, 1)</tt>, then {@link #invertAffine()} can be used instead of this method.
     * 
     * @see #invertAffine()
     * 
     * @return this
     */
    public Matrix4d invert() {
        return invert(this);
    }

    /**
     * Invert <code>this</code> matrix and store the result in <code>dest</code>.
     * <p>
     * If <code>this</code> matrix represents an {@link #isAffine() affine} transformation, such as translation, rotation, scaling and shearing,
     * and thus its last row is equal to <tt>(0, 0, 0, 1)</tt>, then {@link #invertAffine(Matrix4d)} can be used instead of this method.
     * 
     * @see #invertAffine(Matrix4d)
     * 
     * @param dest
     *             will hold the result
     * @return dest
     */
    public Matrix4d invert(Matrix4d dest) {
        double a = ms[M00] * ms[M11] - ms[M01] * ms[M10];
        double b = ms[M00] * ms[M12] - ms[M02] * ms[M10];
        double c = ms[M00] * ms[M13] - ms[M03] * ms[M10];
        double d = ms[M01] * ms[M12] - ms[M02] * ms[M11];
        double e = ms[M01] * ms[M13] - ms[M03] * ms[M11];
        double f = ms[M02] * ms[M13] - ms[M03] * ms[M12];
        double g = ms[M20] * ms[M31] - ms[M21] * ms[M30];
        double h = ms[M20] * ms[M32] - ms[M22] * ms[M30];
        double i = ms[M20] * ms[M33] - ms[M23] * ms[M30];
        double j = ms[M21] * ms[M32] - ms[M22] * ms[M31];
        double k = ms[M21] * ms[M33] - ms[M23] * ms[M31];
        double l = ms[M22] * ms[M33] - ms[M23] * ms[M32];
        double det = a * l - b * k + c * j + d * i - e * h + f * g;
        det = 1.0 / det;
        dest.set(( ms[M11] * l - ms[M12] * k + ms[M13] * j) * det,
                 (-ms[M01] * l + ms[M02] * k - ms[M03] * j) * det,
                 ( ms[M31] * f - ms[M32] * e + ms[M33] * d) * det,
                 (-ms[M21] * f + ms[M22] * e - ms[M23] * d) * det,
                 (-ms[M10] * l + ms[M12] * i - ms[M13] * h) * det,
                 ( ms[M00] * l - ms[M02] * i + ms[M03] * h) * det,
                 (-ms[M30] * f + ms[M32] * c - ms[M33] * b) * det,
                 ( ms[M20] * f - ms[M22] * c + ms[M23] * b) * det,
                 ( ms[M10] * k - ms[M11] * i + ms[M13] * g) * det,
                 (-ms[M00] * k + ms[M01] * i - ms[M03] * g) * det,
                 ( ms[M30] * e - ms[M31] * c + ms[M33] * a) * det,
                 (-ms[M20] * e + ms[M21] * c - ms[M23] * a) * det,
                 (-ms[M10] * j + ms[M11] * h - ms[M12] * g) * det,
                 ( ms[M00] * j - ms[M01] * h + ms[M02] * g) * det,
                 (-ms[M30] * d + ms[M31] * b - ms[M32] * a) * det,
                 ( ms[M20] * d - ms[M21] * b + ms[M22] * a) * det);
        return dest;
    }

    /**
     * If <code>this</code> is a perspective projection matrix obtained via one of the {@link #perspective(double, double, double, double) perspective()} methods
     * or via {@link #setPerspective(double, double, double, double) setPerspective()}, that is, if <code>this</code> is a symmetrical perspective frustum transformation,
     * then this method builds the inverse of <code>this</code> and stores it into the given <code>dest</code>.
     * <p>
     * This method can be used to quickly obtain the inverse of a perspective projection matrix when being obtained via {@link #perspective(double, double, double, double) perspective()}.
     * 
     * @see #perspective(double, double, double, double)
     * 
     * @param dest
     *          will hold the inverse of <code>this</code>
     * @return dest
     */
    public Matrix4d invertPerspective(Matrix4d dest) {
        double a =  1.0 / (ms[M00] * ms[M11]);
        double l = -1.0 / (ms[M23] * ms[M32]);
        dest.set(ms[M11] * a, 0, 0, 0,
                 0, ms[M00] * a, 0, 0,
                 0, 0, 0, -ms[M23] * l,
                 0, 0, -ms[M32] * l, ms[M22] * l);
        return dest;
    }

    /**
     * If <code>this</code> is a perspective projection matrix obtained via one of the {@link #perspective(double, double, double, double) perspective()} methods
     * or via {@link #setPerspective(double, double, double, double) setPerspective()}, that is, if <code>this</code> is a symmetrical perspective frustum transformation,
     * then this method builds the inverse of <code>this</code>.
     * <p>
     * This method can be used to quickly obtain the inverse of a perspective projection matrix when being obtained via {@link #perspective(double, double, double, double) perspective()}.
     * 
     * @see #perspective(double, double, double, double)
     * 
     * @return this
     */
    public Matrix4d invertPerspective() {
        return invertPerspective(this);
    }

    /**
     * If <code>this</code> is an arbitrary perspective projection matrix obtained via one of the {@link #frustum(double, double, double, double, double, double) frustum()}  methods
     * or via {@link #setFrustum(double, double, double, double, double, double) setFrustum()},
     * then this method builds the inverse of <code>this</code> and stores it into the given <code>dest</code>.
     * <p>
     * This method can be used to quickly obtain the inverse of a perspective projection matrix.
     * <p>
     * If this matrix represents a symmetric perspective frustum transformation, as obtained via {@link #perspective(double, double, double, double) perspective()}, then
     * {@link #invertPerspective(Matrix4d)} should be used instead.
     * 
     * @see #frustum(double, double, double, double, double, double)
     * @see #invertPerspective(Matrix4d)
     * 
     * @param dest
     *          will hold the inverse of <code>this</code>
     * @return dest
     */
    public Matrix4d invertFrustum(Matrix4d dest) {
        double invM00 = 1.0 / ms[M00];
        double invM11 = 1.0 / ms[M11];
        double invM23 = 1.0 / ms[M23];
        double invM32 = 1.0 / ms[M32];
        dest.set(invM00, 0, 0, 0,
                 0, invM11, 0, 0,
                 0, 0, 0, invM32,
                 -ms[M20] * invM00 * invM23, -ms[M21] * invM11 * invM23, invM23, -ms[M22] * invM23 * invM32);
        return dest;
    }

    /**
     * If <code>this</code> is an arbitrary perspective projection matrix obtained via one of the {@link #frustum(double, double, double, double, double, double) frustum()}  methods
     * or via {@link #setFrustum(double, double, double, double, double, double) setFrustum()},
     * then this method builds the inverse of <code>this</code>.
     * <p>
     * This method can be used to quickly obtain the inverse of a perspective projection matrix.
     * <p>
     * If this matrix represents a symmetric perspective frustum transformation, as obtained via {@link #perspective(double, double, double, double) perspective()}, then
     * {@link #invertPerspective()} should be used instead.
     * 
     * @see #frustum(double, double, double, double, double, double)
     * @see #invertPerspective()
     * 
     * @return this
     */
    public Matrix4d invertFrustum() {
        return invertFrustum(this);
    }

    /**
     * Invert <code>this</code> orthographic projection matrix and store the result into the given <code>dest</code>.
     * <p>
     * This method can be used to quickly obtain the inverse of an orthographic projection matrix.
     * 
     * @param dest
     *          will hold the inverse of <code>this</code>
     * @return dest
     */
    public Matrix4d invertOrtho(Matrix4d dest) {
        double invM00 = 1.0 / ms[M00];
        double invM11 = 1.0 / ms[M11];
        double invM22 = 1.0 / ms[M22];
        dest.set(invM00, 0, 0, 0,
                 0, invM11, 0, 0,
                 0, 0, invM22, 0,
                 -ms[M30] * invM00, -ms[M31] * invM11, -ms[M32] * invM22, 1);
        return dest;
    }

    /**
     * Invert <code>this</code> orthographic projection matrix.
     * <p>
     * This method can be used to quickly obtain the inverse of an orthographic projection matrix.
     * 
     * @return this
     */
    public Matrix4d invertOrtho() {
        return invertOrtho(this);
    }

    /**
     * If <code>this</code> is a perspective projection matrix obtained via one of the {@link #perspective(double, double, double, double) perspective()} methods
     * or via {@link #setPerspective(double, double, double, double) setPerspective()}, that is, if <code>this</code> is a symmetrical perspective frustum transformation
     * and the given <code>view</code> matrix is {@link #isAffine() affine} and has unit scaling (for example by being obtained via {@link #lookAt(double, double, double, double, double, double, double, double, double) lookAt()}),
     * then this method builds the inverse of <tt>this * view</tt> and stores it into the given <code>dest</code>.
     * <p>
     * This method can be used to quickly obtain the inverse of the combination of the view and projection matrices, when both were obtained
     * via the common methods {@link #perspective(double, double, double, double) perspective()} and {@link #lookAt(double, double, double, double, double, double, double, double, double) lookAt()} or
     * other methods, that build affine matrices, such as {@link #translate(double, double, double) translate} and {@link #rotate(double, double, double, double)}, except for {@link #scale(double, double, double) scale()}.
     * <p>
     * For the special cases of the matrices <code>this</code> and <code>view</code> mentioned above this method, this method is equivalent to the following code:
     * <pre>
     * dest.set(this).mul(view).invert();
     * </pre>
     * 
     * @param view
     *          the view transformation (must be {@link #isAffine() affine} and have unit scaling)
     * @param dest
     *          will hold the inverse of <tt>this * view</tt>
     * @return dest
     */
    public Matrix4d invertPerspectiveView(Matrix4d view, Matrix4d dest) {
        double a =  1.0 / (ms[M00] * ms[M11]);
        double l = -1.0 / (ms[M23] * ms[M32]);
        double pms00 =  ms[M11] * a;
        double pms11 =  ms[M00] * a;
        double pms23 = -ms[M23] * l;
        double pms32 = -ms[M32] * l;
        double pms33 =  ms[M22] * l;
        double vms30 = -view.ms[M00] * view.ms[M30] - view.ms[M01] * view.ms[M31] - view.ms[M02] * view.ms[M32];
        double vms31 = -view.ms[M10] * view.ms[M30] - view.ms[M11] * view.ms[M31] - view.ms[M12] * view.ms[M32];
        double vms32 = -view.ms[M20] * view.ms[M30] - view.ms[M21] * view.ms[M31] - view.ms[M22] * view.ms[M32];
        dest.set(view.ms[M00] * pms00, view.ms[M10] * pms00, view.ms[M20] * pms00, 0.0,
                 view.ms[M01] * pms11, view.ms[M11] * pms11, view.ms[M21] * pms11, 0.0,
                 vms30 * pms23, vms31 * pms23, vms32 * pms23, pms23,
                 view.ms[M02] * pms32 + vms30 * pms33, view.ms[M12] * pms32 + vms31 * pms33, view.ms[M22] * pms32 + vms32 * pms33, pms33);
        return dest;
    }

    /**
     * Invert this matrix by assuming that it is an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and write the result into <code>dest</code>.
     * <p>
     * Note that if <code>this</code> matrix also has unit scaling, then the method {@link #invertAffineUnitScale(Matrix4d)} should be used instead.
     * 
     * @see #invertAffineUnitScale(Matrix4d)
     * 
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d invertAffine(Matrix4d dest) {
        double s = determinantAffine();
        s = 1.0 / s;
        double ms1022 = ms[M10] * ms[M22];
        double ms1021 = ms[M10] * ms[M21];
        double ms1002 = ms[M10] * ms[M02];
        double ms1001 = ms[M10] * ms[M01];
        double ms1122 = ms[M11] * ms[M22];
        double ms1120 = ms[M11] * ms[M20];
        double ms1102 = ms[M11] * ms[M02];
        double ms1100 = ms[M11] * ms[M00];
        double ms1221 = ms[M12] * ms[M21];
        double ms1220 = ms[M12] * ms[M20];
        double ms1201 = ms[M12] * ms[M01];
        double ms1200 = ms[M12] * ms[M00];
        double ms2002 = ms[M20] * ms[M02];
        double ms2001 = ms[M20] * ms[M01];
        double ms2102 = ms[M21] * ms[M02];
        double ms2100 = ms[M21] * ms[M00];
        double ms2201 = ms[M22] * ms[M01];
        double ms2200 = ms[M22] * ms[M00];
        dest.set((ms1122 - ms1221) * s,
                 (ms2102 - ms2201) * s,
                 (ms1201 - ms1102) * s,
                 0.0,
                 (ms1220 - ms1022) * s,
                 (ms2200 - ms2002) * s,
                 (ms1002 - ms1200) * s,
                 0.0,
                 (ms1021 - ms1120) * s,
                 (ms2001 - ms2100) * s,
                 (ms1100 - ms1001) * s,
                 0.0,
                 (ms1022 * ms[M31] - ms1021 * ms[M32] + ms1120 * ms[M32] - ms1122 * ms[M30] + ms1221 * ms[M30] - ms1220 * ms[M31]) * s,
                 (ms2002 * ms[M31] - ms2001 * ms[M32] + ms2100 * ms[M32] - ms2102 * ms[M30] + ms2201 * ms[M30] - ms2200 * ms[M31]) * s,
                 (ms1102 * ms[M30] - ms1201 * ms[M30] + ms1200 * ms[M31] - ms1002 * ms[M31] + ms1001 * ms[M32] - ms1100 * ms[M32]) * s,
                 1.0);
        return dest;
    }

    /**
     * Invert this matrix by assuming that it is an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>).
     * <p>
     * Note that if <code>this</code> matrix also has unit scaling, then the method {@link #invertAffineUnitScale()} should be used instead.
     * 
     * @see #invertAffineUnitScale()
     * 
     * @return this
     */
    public Matrix4d invertAffine() {
        return invertAffine(this);
    }

    /**
     * Invert this matrix by assuming that it is an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and has unit scaling (i.e. {@link #transformDirection(Vector3d) transformDirection} does not change the {@link Vector3d#length() length} of the vector)
     * and write the result into <code>dest</code>.
     * <p>
     * Reference: <a href="http://www.gamedev.net/topic/425118-inverse--matrix/">http://www.gamedev.net/</a>
     * 
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d invertAffineUnitScale(Matrix4d dest) {
        dest.set(ms[M00], ms[M10], ms[M20], 0.0,
                 ms[M01], ms[M11], ms[M21], 0.0,
                 ms[M02], ms[M12], ms[M22], 0.0,
                 -ms[M00] * ms[M30] - ms[M01] * ms[M31] - ms[M02] * ms[M32],
                 -ms[M10] * ms[M30] - ms[M11] * ms[M31] - ms[M12] * ms[M32],
                 -ms[M20] * ms[M30] - ms[M21] * ms[M31] - ms[M22] * ms[M32],
                 1.0);
        return dest;
    }

    /**
     * Invert this matrix by assuming that it is an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and has unit scaling (i.e. {@link #transformDirection(Vector3d) transformDirection} does not change the {@link Vector3d#length() length} of the vector).
     * <p>
     * Reference: <a href="http://www.gamedev.net/topic/425118-inverse--matrix/">http://www.gamedev.net/</a>
     * 
     * @return this
     */
    public Matrix4d invertAffineUnitScale() {
        return invertAffineUnitScale(this);
    }

    /**
     * Invert this matrix by assuming that it is an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and has unit scaling (i.e. {@link #transformDirection(Vector3d) transformDirection} does not change the {@link Vector3d#length() length} of the vector),
     * as is the case for matrices built via {@link #lookAt(Vector3d, Vector3d, Vector3d)} and their overloads, and write the result into <code>dest</code>.
     * <p>
     * This method is equivalent to calling {@link #invertAffineUnitScale(Matrix4d)}
     * <p>
     * Reference: <a href="http://www.gamedev.net/topic/425118-inverse--matrix/">http://www.gamedev.net/</a>
     * 
     * @see #invertAffineUnitScale(Matrix4d)
     * 
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d invertLookAt(Matrix4d dest) {
        return invertAffineUnitScale(dest);
    }

    /**
     * Invert this matrix by assuming that it is an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and has unit scaling (i.e. {@link #transformDirection(Vector3d) transformDirection} does not change the {@link Vector3d#length() length} of the vector),
     * as is the case for matrices built via {@link #lookAt(Vector3d, Vector3d, Vector3d)} and their overloads.
     * <p>
     * This method is equivalent to calling {@link #invertAffineUnitScale()}
     * <p>
     * Reference: <a href="http://www.gamedev.net/topic/425118-inverse--matrix/">http://www.gamedev.net/</a>
     * 
     * @see #invertAffineUnitScale()
     * 
     * @return this
     */
    public Matrix4d invertLookAt() {
        return invertAffineUnitScale(this);
    }

    /**
     * Transpose this matrix.
     * 
     * @return this
     */
    public Matrix4d transpose() {
        return transpose(this);
    }

    /**
     * Transpose <code>this</code> matrix and store the result into <code>dest</code>.
     * 
     * @param dest
     *             will hold the result
     * @return dest
     */
    public Matrix4d transpose(Matrix4d dest) {
        dest.set(ms[M00], ms[M10], ms[M20], ms[M30],
                 ms[M01], ms[M11], ms[M21], ms[M31],
                 ms[M02], ms[M12], ms[M22], ms[M32],
                 ms[M03], ms[M13], ms[M23], ms[M33]);
        return dest;
    }

    /**
     * Transpose only the upper left 3x3 submatrix of this matrix and set the rest of the matrix elements to identity.
     * 
     * @return this
     */
    public Matrix4d transpose3x3() {
        return transpose3x3(this);
    }

    /**
     * Transpose only the upper left 3x3 submatrix of this matrix and store the result in <code>dest</code>.
     * <p>
     * All other matrix elements of <code>dest</code> will be set to identity.
     * 
     * @param dest
     *             will hold the result
     * @return dest
     */
    public Matrix4d transpose3x3(Matrix4d dest) {
        dest.set(ms[M00], ms[M10], ms[M20], 0.0,
                 ms[M01], ms[M11], ms[M21], 0.0,
                 ms[M02], ms[M12], ms[M22], 0.0,
                 0.0, 0.0, 0.0, 1.0);
        return dest;
    }

    /**
     * Transpose only the upper left 3x3 submatrix of this matrix and store the result in <code>dest</code>.
     * 
     * @param dest
     *             will hold the result
     * @return dest
     */
    public Matrix3d transpose3x3(Matrix3d dest) {
        dest.ms[Matrix3d.M00] = ms[M00];
        dest.ms[Matrix3d.M01] = ms[M10];
        dest.ms[Matrix3d.M02] = ms[M20];
        dest.ms[Matrix3d.M10] = ms[M01];
        dest.ms[Matrix3d.M11] = ms[M11];
        dest.ms[Matrix3d.M12] = ms[M21];
        dest.ms[Matrix3d.M20] = ms[M02];
        dest.ms[Matrix3d.M21] = ms[M12];
        dest.ms[Matrix3d.M22] = ms[M22];
        return dest;
    }

    /**
     * Set this matrix to be a simple translation matrix.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional translation.
     * 
     * @param x
     *          the offset to translate in x
     * @param y
     *          the offset to translate in y
     * @param z
     *          the offset to translate in z
     * @return this
     */
    public Matrix4d translation(double x, double y, double z) {
        ms[M00] = 1.0;
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = 1.0;
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        ms[M22] = 1.0;
        ms[M23] = 0.0;
        ms[M30] = x;
        ms[M31] = y;
        ms[M32] = z;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to be a simple translation matrix.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional translation.
     * 
     * @param offset
     *              the offsets in x, y and z to translate
     * @return this
     */
    public Matrix4d translation(Vector3f offset) {
        return translation(offset.x, offset.y, offset.z);
    }

    /**
     * Set this matrix to be a simple translation matrix.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional translation.
     *
     * @param offset
     *              the offsets in x, y and z to translate
     * @return this
     */
    public Matrix4d translation(Vector3d offset) {
        return translation(offset.x, offset.y, offset.z);
    }

    /**
     * Set only the translation components <tt>(ms[M30], ms[M31], ms[M32])</tt> of this matrix to the given values <tt>(x, y, z)</tt>.
     * <p>
     * To build a translation matrix instead, use {@link #translation(double, double, double)}.
     * To apply a translation to another matrix, use {@link #translate(double, double, double)}.
     * 
     * @see #translation(double, double, double)
     * @see #translate(double, double, double)
     * 
     * @param x
     *          the units to translate in x
     * @param y
     *          the units to translate in y
     * @param z
     *          the units to translate in z
     * @return this
     */
    public Matrix4d setTranslation(double x, double y, double z) {
        ms[M30] = x;
        ms[M31] = y;
        ms[M32] = z;
        return this;
    }

    /**
     * Set only the translation components <tt>(ms[M30], ms[M31], ms[M32])</tt> of this matrix to the given values <tt>(xyz.x, xyz.y, xyz.z)</tt>.
     * <p>
     * To build a translation matrix instead, use {@link #translation(Vector3d)}.
     * To apply a translation to another matrix, use {@link #translate(Vector3d)}.
     * 
     * @see #translation(Vector3d)
     * @see #translate(Vector3d)
     * 
     * @param xyz
     *          the units to translate in <tt>(x, y, z)</tt>
     * @return this
     */
    public Matrix4d setTranslation(Vector3d xyz) {
        ms[M30] = xyz.x;
        ms[M31] = xyz.y;
        ms[M32] = xyz.z;
        return this;
    }

    /**
     * Get only the translation components <tt>(ms[M30], ms[M31], ms[M32])</tt> of this matrix and store them in the given vector <code>xyz</code>.
     * 
     * @param dest
     *          will hold the translation components of this matrix
     * @return dest
     */
    public Vector3d getTranslation(Vector3d dest) {
        dest.x = ms[M30];
        dest.y = ms[M31];
        dest.z = ms[M32];
        return dest;
    }

    /**
     * Get the scaling factors of <code>this</code> matrix for the three base axes.
     * 
     * @param dest
     *          will hold the scaling factors for <tt>x</tt>, <tt>y</tt> and <tt>z</tt>
     * @return dest
     */
    public Vector3d getScale(Vector3d dest) {
        dest.x = Math.sqrt(ms[M00] * ms[M00] + ms[M01] * ms[M01] + ms[M02] * ms[M02]);
        dest.y = Math.sqrt(ms[M10] * ms[M10] + ms[M11] * ms[M11] + ms[M12] * ms[M12]);
        dest.z = Math.sqrt(ms[M20] * ms[M20] + ms[M21] * ms[M21] + ms[M22] * ms[M22]);
        return dest;
    }

    /**
     * Return a string representation of this matrix.
     * <p>
     * This method creates a new {@link DecimalFormat} on every invocation with the format string "<tt>  0.000E0; -</tt>".
     * 
     * @return the string representation
     */
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("  0.000E0; -"); //$NON-NLS-1$
        return toString(formatter).replaceAll("E(\\d+)", "E+$1"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Return a string representation of this matrix by formatting the matrix elements with the given {@link NumberFormat}.
     * 
     * @param formatter
     *          the {@link NumberFormat} used to format the matrix values with
     * @return the string representation
     */
    public String toString(NumberFormat formatter) {
        return formatter.format(ms[M00]) + formatter.format(ms[M10]) + formatter.format(ms[M20]) + formatter.format(ms[M30]) + "\n" //$NON-NLS-1$
             + formatter.format(ms[M01]) + formatter.format(ms[M11]) + formatter.format(ms[M21]) + formatter.format(ms[M31]) + "\n" //$NON-NLS-1$
             + formatter.format(ms[M02]) + formatter.format(ms[M12]) + formatter.format(ms[M22]) + formatter.format(ms[M32]) + "\n" //$NON-NLS-1$
             + formatter.format(ms[M03]) + formatter.format(ms[M13]) + formatter.format(ms[M23]) + formatter.format(ms[M33]) + "\n"; //$NON-NLS-1$
    }

    /**
     * Get the current values of <code>this</code> matrix and store them into
     * <code>dest</code>.
     * <p>
     * This is the reverse method of {@link #set(Matrix4d)} and allows to obtain
     * intermediate calculation results when chaining multiple transformations.
     * 
     * @see #set(Matrix4d)
     * 
     * @param dest
     *          the destination matrix
     * @return the passed in destination
     */
    public Matrix4d get(Matrix4d dest) {
        return dest.set(this);
    }

    /**
     * Get the current values of the upper left 3x3 submatrix of <code>this</code> matrix and store them into
     * <code>dest</code>.
     * 
     * @param dest
     *            the destination matrix
     * @return the passed in destination
     */
    public Matrix3d get3x3(Matrix3d dest) {
        return dest.set(this);
    }

    /**
     * Get the current values of <code>this</code> matrix and store the represented rotation
     * into the given {@link Quaternionf}.
     * <p>
     * This method assumes that the first three column vectors of the upper left 3x3 submatrix are not normalized and
     * thus allows to ignore any additional scaling factor that is applied to the matrix.
     * 
     * @see Quaternionf#setFromUnnormalized(Matrix4d)
     * 
     * @param dest
     *          the destination {@link Quaternionf}
     * @return the passed in destination
     */
    public Quaternionf getUnnormalizedRotation(Quaternionf dest) {
        return dest.setFromUnnormalized(this);
    }

    /**
     * Get the current values of <code>this</code> matrix and store the represented rotation
     * into the given {@link Quaternionf}.
     * <p>
     * This method assumes that the first three column vectors of the upper left 3x3 submatrix are normalized.
     * 
     * @see Quaternionf#setFromNormalized(Matrix4d)
     * 
     * @param dest
     *          the destination {@link Quaternionf}
     * @return the passed in destination
     */
    public Quaternionf getNormalizedRotation(Quaternionf dest) {
        return dest.setFromNormalized(this);
    }

    /**
     * Get the current values of <code>this</code> matrix and store the represented rotation
     * into the given {@link Quaterniond}.
     * <p>
     * This method assumes that the first three column vectors of the upper left 3x3 submatrix are not normalized and
     * thus allows to ignore any additional scaling factor that is applied to the matrix.
     * 
     * @see Quaterniond#setFromUnnormalized(Matrix4d)
     * 
     * @param dest
     *          the destination {@link Quaterniond}
     * @return the passed in destination
     */
    public Quaterniond getUnnormalizedRotation(Quaterniond dest) {
        return dest.setFromUnnormalized(this);
    }

    /**
     * Get the current values of <code>this</code> matrix and store the represented rotation
     * into the given {@link Quaterniond}.
     * <p>
     * This method assumes that the first three column vectors of the upper left 3x3 submatrix are normalized.
     * 
     * @see Quaterniond#setFromNormalized(Matrix4d)
     * 
     * @param dest
     *          the destination {@link Quaterniond}
     * @return the passed in destination
     */
    public Quaterniond getNormalizedRotation(Quaterniond dest) {
        return dest.setFromNormalized(this);
    }

    /**
     * Store this matrix in column-major order into the supplied {@link DoubleBuffer} at the current
     * buffer {@link DoubleBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given DoubleBuffer.
     * <p>
     * In order to specify the offset into the DoubleBuffer at which
     * the matrix is stored, use {@link #get(int, DoubleBuffer)}, taking
     * the absolute position as parameter.
     * 
     * @see #get(int, DoubleBuffer)
     * 
     * @param buffer
     *            will receive the values of this matrix in column-major order at its current position
     * @return the passed in buffer
     */
    public DoubleBuffer get(DoubleBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    /**
     * Store this matrix in column-major order into the supplied {@link DoubleBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given {@link DoubleBuffer}.
     * 
     * @param index
     *            the absolute position into the {@link DoubleBuffer}
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    public DoubleBuffer get(int index, DoubleBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    /**
     * Store this matrix in column-major order into the supplied {@link FloatBuffer} at the current
     * buffer {@link FloatBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given
     * FloatBuffer.
     * <p>
     * In order to specify the offset into the FloatBuffer at which
     * the matrix is stored, use {@link #get(int, FloatBuffer)}, taking
     * the absolute position as parameter.
     * <p>
     * Please note that due to this matrix storing double values those values will potentially
     * lose precision when they are converted to float values before being put into the given FloatBuffer.
     * 
     * @see #get(int, FloatBuffer)
     * 
     * @param buffer
     *            will receive the values of this matrix in column-major order at its current position
     * @return the passed in buffer
     */
    public FloatBuffer get(FloatBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    /**
     * Store this matrix in column-major order into the supplied {@link FloatBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given FloatBuffer.
     * <p>
     * Please note that due to this matrix storing double values those values will potentially
     * lose precision when they are converted to float values before being put into the given FloatBuffer.
     * 
     * @param index
     *            the absolute position into the FloatBuffer
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    public FloatBuffer get(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.putf(this, index, buffer);
        return buffer;
    }

    /**
     * Store this matrix in column-major order into the supplied {@link ByteBuffer} at the current
     * buffer {@link ByteBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * In order to specify the offset into the ByteBuffer at which
     * the matrix is stored, use {@link #get(int, ByteBuffer)}, taking
     * the absolute position as parameter.
     * 
     * @see #get(int, ByteBuffer)
     * 
     * @param buffer
     *            will receive the values of this matrix in column-major order at its current position
     * @return the passed in buffer
     */
    public ByteBuffer get(ByteBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    /**
     * Store this matrix in column-major order into the supplied {@link ByteBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * 
     * @param index
     *            the absolute position into the ByteBuffer
     * @param buffer
     *            will receive the values of this matrix in column-major order
     * @return the passed in buffer
     */
    public ByteBuffer get(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    /**
     * Store the elements of this matrix as float values in column-major order into the supplied {@link ByteBuffer} at the current
     * buffer {@link ByteBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * Please note that due to this matrix storing double values those values will potentially
     * lose precision when they are converted to float values before being put into the given ByteBuffer.
     * <p>
     * In order to specify the offset into the ByteBuffer at which
     * the matrix is stored, use {@link #getFloats(int, ByteBuffer)}, taking
     * the absolute position as parameter.
     * 
     * @see #getFloats(int, ByteBuffer)
     * 
     * @param buffer
     *            will receive the elements of this matrix as float values in column-major order at its current position
     * @return the passed in buffer
     */
    public ByteBuffer getFloats(ByteBuffer buffer) {
        return getFloats(buffer.position(), buffer);
    }

    /**
     * Store the elements of this matrix as float values in column-major order into the supplied {@link ByteBuffer}
     * starting at the specified absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * Please note that due to this matrix storing double values those values will potentially
     * lose precision when they are converted to float values before being put into the given ByteBuffer.
     * 
     * @param index
     *            the absolute position into the ByteBuffer
     * @param buffer
     *            will receive the elements of this matrix as float values in column-major order
     * @return the passed in buffer
     */
    public ByteBuffer getFloats(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.putf(this, index, buffer);
        return buffer;
    }

    /**
     * Store this matrix into the supplied double array in column-major order at the given offset.
     * 
     * @param arr
     *          the array to write the matrix values into
     * @param offset
     *          the offset into the array
     * @return the passed in array
     */
    public double[] get(double[] arr, int offset) {
        arr[offset+0]  = ms[M00];
        arr[offset+1]  = ms[M01];
        arr[offset+2]  = ms[M02];
        arr[offset+3]  = ms[M03];
        arr[offset+4]  = ms[M10];
        arr[offset+5]  = ms[M11];
        arr[offset+6]  = ms[M12];
        arr[offset+7]  = ms[M13];
        arr[offset+8]  = ms[M20];
        arr[offset+9]  = ms[M21];
        arr[offset+10] = ms[M22];
        arr[offset+11] = ms[M23];
        arr[offset+12] = ms[M30];
        arr[offset+13] = ms[M31];
        arr[offset+14] = ms[M32];
        arr[offset+15] = ms[M33];
        return arr;
    }

    /**
     * Store this matrix into the supplied double array in column-major order.
     * <p>
     * In order to specify an explicit offset into the array, use the method {@link #get(double[], int)}.
     * 
     * @see #get(double[], int)
     * 
     * @param arr
     *          the array to write the matrix values into
     * @return the passed in array
     */
    public double[] get(double[] arr) {
        return get(arr, 0);
    }

    /**
     * Store the elements of this matrix as float values in column-major order into the supplied float array at the given offset.
     * <p>
     * Please note that due to this matrix storing double values those values will potentially
     * lose precision when they are converted to float values before being put into the given float array.
     * 
     * @param arr
     *          the array to write the matrix values into
     * @param offset
     *          the offset into the array
     * @return the passed in array
     */
    public float[] get(float[] arr, int offset) {
        arr[offset+0]  = (float)ms[M00];
        arr[offset+1]  = (float)ms[M01];
        arr[offset+2]  = (float)ms[M02];
        arr[offset+3]  = (float)ms[M03];
        arr[offset+4]  = (float)ms[M10];
        arr[offset+5]  = (float)ms[M11];
        arr[offset+6]  = (float)ms[M12];
        arr[offset+7]  = (float)ms[M13];
        arr[offset+8]  = (float)ms[M20];
        arr[offset+9]  = (float)ms[M21];
        arr[offset+10] = (float)ms[M22];
        arr[offset+11] = (float)ms[M23];
        arr[offset+12] = (float)ms[M30];
        arr[offset+13] = (float)ms[M31];
        arr[offset+14] = (float)ms[M32];
        arr[offset+15] = (float)ms[M33];
        return arr;
    }

    /**
     * Store the elements of this matrix as float values in column-major order into the supplied float array.
     * <p>
     * Please note that due to this matrix storing double values those values will potentially
     * lose precision when they are converted to float values before being put into the given float array.
     * <p>
     * In order to specify an explicit offset into the array, use the method {@link #get(float[], int)}.
     * 
     * @see #get(float[], int)
     * 
     * @param arr
     *          the array to write the matrix values into
     * @return the passed in array
     */
    public float[] get(float[] arr) {
        return get(arr, 0);
    }

    /**
     * Set all the values within this matrix to 0.
     * 
     * @return this
     */
    public Matrix4d zero() {
        ms[M00] = 0.0;
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = 0.0;
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        ms[M22] = 0.0;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 0.0;
        return this;
    }

    /**
     * Set this matrix to be a simple scale matrix, which scales all axes uniformly by the given factor.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional scaling.
     * <p>
     * In order to post-multiply a scaling transformation directly to a
     * matrix, use {@link #scale(double) scale()} instead.
     * 
     * @see #scale(double)
     * 
     * @param factor
     *             the scale factor in x, y and z
     * @return this
     */
    public Matrix4d scaling(double factor) {
        ms[M00] = factor;
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = factor;
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        ms[M22] = factor;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to be a simple scale matrix.
     * 
     * @param x
     *          the scale in x
     * @param y
     *          the scale in y
     * @param z
     *          the scale in z         
     * @return this
     */
    public Matrix4d scaling(double x, double y, double z) {
        ms[M00] = x;
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = y;
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        ms[M22] = z;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to be a simple scale matrix which scales the base axes by
     * <tt>xyz.x</tt>, <tt>xyz.y</tt> and <tt>xyz.z</tt>, respectively.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional scaling.
     * <p>
     * In order to post-multiply a scaling transformation directly to a
     * matrix use {@link #scale(Vector3d) scale()} instead.
     * 
     * @see #scale(Vector3d)
     * 
     * @param xyz
     *             the scale in x, y and z, respectively
     * @return this
     */
    public Matrix4d scaling(Vector3d xyz) {
        return scaling(xyz.x, xyz.y, xyz.z);
    }

    /**
     * Set this matrix to a rotation matrix which rotates the given radians about a given axis.
     * <p>
     * From <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle">Wikipedia</a>
     * 
     * @param angle
     *          the angle in radians
     * @param x
     *          the x-coordinate of the axis to rotate about
     * @param y
     *          the y-coordinate of the axis to rotate about
     * @param z
     *          the z-coordinate of the axis to rotate about
     * @return this
     */
    public Matrix4d rotation(double angle, double x, double y, double z) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double C = 1.0 - cos;
        double xy = x * y, xz = x * z, yz = y * z;
        ms[M00] = cos + x * x * C;
        ms[M10] = xy * C - z * sin;
        ms[M20] = xz * C + y * sin;
        ms[M30] = 0.0;
        ms[M01] = xy * C + z * sin;
        ms[M11] = cos + y * y * C;
        ms[M21] = yz * C - x * sin;
        ms[M31] = 0.0;
        ms[M02] = xz * C - y * sin;
        ms[M12] = yz * C + x * sin;
        ms[M22] = cos + z * z * C;
        ms[M32] = 0.0;
        ms[M03] = 0.0;
        ms[M13] = 0.0;
        ms[M23] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to a rotation transformation about the X axis.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">http://en.wikipedia.org</a>
     * 
     * @param ang
     *            the angle in radians
     * @return this
     */
    public Matrix4d rotationX(double ang) {
        double sin, cos;
        if (ang == Math.PI || ang == -Math.PI) {
            cos = -1.0;
            sin = 0.0;
        } else if (ang == Math.PI * 0.5 || ang == -Math.PI * 1.5) {
            cos = 0.0;
            sin = 1.0;
        } else if (ang == -Math.PI * 0.5 || ang == Math.PI * 1.5) {
            cos = 0.0;
            sin = -1.0;
        } else {
            cos = Math.cos(ang);
            sin = Math.sin(ang);
        }
        ms[M00] = 1.0;
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = cos;
        ms[M12] = sin;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = -sin;
        ms[M22] = cos;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to a rotation transformation about the Y axis.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">http://en.wikipedia.org</a>
     * 
     * @param ang
     *            the angle in radians
     * @return this
     */
    public Matrix4d rotationY(double ang) {
        double sin, cos;
        if (ang == Math.PI || ang == -Math.PI) {
            cos = -1.0;
            sin = 0.0;
        } else if (ang == Math.PI * 0.5 || ang == -Math.PI * 1.5) {
            cos = 0.0;
            sin = 1.0;
        } else if (ang == -Math.PI * 0.5 || ang == Math.PI * 1.5) {
            cos = 0.0;
            sin = -1.0;
        } else {
            cos = Math.cos(ang);
            sin = Math.sin(ang);
        }
        ms[M00] = cos;
        ms[M01] = 0.0;
        ms[M02] = -sin;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = 1.0;
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = sin;
        ms[M21] = 0.0;
        ms[M22] = cos;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to a rotation transformation about the Z axis.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">http://en.wikipedia.org</a>
     * 
     * @param ang
     *            the angle in radians
     * @return this
     */
    public Matrix4d rotationZ(double ang) {
        double sin, cos;
        if (ang == Math.PI || ang == -Math.PI) {
            cos = -1.0;
            sin = 0.0;
        } else if (ang == Math.PI * 0.5 || ang == -Math.PI * 1.5) {
            cos = 0.0;
            sin = 1.0;
        } else if (ang == -Math.PI * 0.5 || ang == Math.PI * 1.5) {
            cos = 0.0;
            sin = -1.0;
        } else {
            cos = Math.cos(ang);
            sin = Math.sin(ang);
        }
        ms[M00] = cos;
        ms[M01] = sin;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = -sin;
        ms[M11] = cos;
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        ms[M22] = 1.0;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to a rotation of <code>angleX</code> radians about the X axis, followed by a rotation
     * of <code>angleY</code> radians about the Y axis and followed by a rotation of <code>angleZ</code> radians about the Z axis.
     * <p>
     * This method is equivalent to calling: <tt>rotationX(angleX).rotateY(angleY).rotateZ(angleZ)</tt>
     * 
     * @param angleX
     *            the angle to rotate about X
     * @param angleY
     *            the angle to rotate about Y
     * @param angleZ
     *            the angle to rotate about Z
     * @return this
     */
    public Matrix4d rotationXYZ(double angleX, double angleY, double angleZ) {
        double cosX =  Math.cos(angleX);
        double sinX =  Math.sin(angleX);
        double cosY =  Math.cos(angleY);
        double sinY =  Math.sin(angleY);
        double cosZ =  Math.cos(angleZ);
        double sinZ =  Math.sin(angleZ);
        double m_sinX = -sinX;
        double m_sinY = -sinY;
        double m_sinZ = -sinZ;

        // rotateX
        double nms11 = cosX;
        double nms12 = sinX;
        double nms21 = m_sinX;
        double nms22 = cosX;
        // rotateY
        double nms00 = cosY;
        double nms01 = nms21 * m_sinY;
        double nms02 = nms22 * m_sinY;
        ms[M20] = sinY;
        ms[M21] = nms21 * cosY;
        ms[M22] = nms22 * cosY;
        ms[M23] = 0.0;
        // rotateZ
        ms[M00] = nms00 * cosZ;
        ms[M01] = nms01 * cosZ + nms11 * sinZ;
        ms[M02] = nms02 * cosZ + nms12 * sinZ;
        ms[M03] = 0.0;
        ms[M10] = nms00 * m_sinZ;
        ms[M11] = nms01 * m_sinZ + nms11 * cosZ;
        ms[M12] = nms02 * m_sinZ + nms12 * cosZ;
        ms[M13] = 0.0;
        // set last column to identity
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to a rotation of <code>angleZ</code> radians about the Z axis, followed by a rotation
     * of <code>angleY</code> radians about the Y axis and followed by a rotation of <code>angleX</code> radians about the X axis.
     * <p>
     * This method is equivalent to calling: <tt>rotationZ(angleZ).rotateY(angleY).rotateX(angleX)</tt>
     * 
     * @param angleZ
     *            the angle to rotate about Z
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @return this
     */
    public Matrix4d rotationZYX(double angleZ, double angleY, double angleX) {
        double cosZ =  Math.cos(angleZ);
        double sinZ =  Math.sin(angleZ);
        double cosY =  Math.cos(angleY);
        double sinY =  Math.sin(angleY);
        double cosX =  Math.cos(angleX);
        double sinX =  Math.sin(angleX);
        double m_sinZ = -sinZ;
        double m_sinY = -sinY;
        double m_sinX = -sinX;

        // rotateZ
        double nms00 = cosZ;
        double nms01 = sinZ;
        double nms10 = m_sinZ;
        double nms11 = cosZ;
        // rotateY
        double nms20 = nms00 * sinY;
        double nms21 = nms01 * sinY;
        double nms22 = cosY;
        ms[M00] = nms00 * cosY;
        ms[M01] = nms01 * cosY;
        ms[M02] = m_sinY;
        ms[M03] = 0.0;
        // rotateX
        ms[M10] = nms10 * cosX + nms20 * sinX;
        ms[M11] = nms11 * cosX + nms21 * sinX;
        ms[M12] = nms22 * sinX;
        ms[M13] = 0.0;
        ms[M20] = nms10 * m_sinX + nms20 * cosX;
        ms[M21] = nms11 * m_sinX + nms21 * cosX;
        ms[M22] = nms22 * cosX;
        ms[M23] = 0.0;
        // set last column to identity
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to a rotation of <code>angleY</code> radians about the Y axis, followed by a rotation
     * of <code>angleX</code> radians about the X axis and followed by a rotation of <code>angleZ</code> radians about the Z axis.
     * <p>
     * This method is equivalent to calling: <tt>rotationY(angleY).rotateX(angleX).rotateZ(angleZ)</tt>
     * 
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @param angleZ
     *            the angle to rotate about Z
     * @return this
     */
    public Matrix4d rotationYXZ(double angleY, double angleX, double angleZ) {
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        double cosZ = Math.cos(angleZ);
        double sinZ = Math.sin(angleZ);
        double m_sinY = -sinY;
        double m_sinX = -sinX;
        double m_sinZ = -sinZ;

        // rotateY
        double nms00 = cosY;
        double nms02 = m_sinY;
        double nms20 = sinY;
        double nms22 = cosY;
        // rotateX
        double nms10 = nms20 * sinX;
        double nms11 = cosX;
        double nms12 = nms22 * sinX;
        ms[M20] = nms20 * cosX;
        ms[M21] = m_sinX;
        ms[M22] = nms22 * cosX;
        ms[M23] = 0.0;
        // rotateZ
        ms[M00] = nms00 * cosZ + nms10 * sinZ;
        ms[M01] = nms11 * sinZ;
        ms[M02] = nms02 * cosZ + nms12 * sinZ;
        ms[M03] = 0.0;
        ms[M10] = nms00 * m_sinZ + nms10 * cosZ;
        ms[M11] = nms11 * cosZ;
        ms[M12] = nms02 * m_sinZ + nms12 * cosZ;
        ms[M13] = 0.0;
        // set last column to identity
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set only the upper left 3x3 submatrix of this matrix to a rotation of <code>angleX</code> radians about the X axis, followed by a rotation
     * of <code>angleY</code> radians about the Y axis and followed by a rotation of <code>angleZ</code> radians about the Z axis.
     * 
     * @param angleX
     *            the angle to rotate about X
     * @param angleY
     *            the angle to rotate about Y
     * @param angleZ
     *            the angle to rotate about Z
     * @return this
     */
    public Matrix4d setRotationXYZ(double angleX, double angleY, double angleZ) {
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double cosZ = Math.cos(angleZ);
        double sinZ = Math.sin(angleZ);
        double m_sinX = -sinX;
        double m_sinY = -sinY;
        double m_sinZ = -sinZ;

        // rotateX
        double nms11 = cosX;
        double nms12 = sinX;
        double nms21 = m_sinX;
        double nms22 = cosX;
        // rotateY
        double nms00 = cosY;
        double nms01 = nms21 * m_sinY;
        double nms02 = nms22 * m_sinY;
        ms[M20] = sinY;
        ms[M21] = nms21 * cosY;
        ms[M22] = nms22 * cosY;
        // rotateZ
        ms[M00] = nms00 * cosZ;
        ms[M01] = nms01 * cosZ + nms11 * sinZ;
        ms[M02] = nms02 * cosZ + nms12 * sinZ;
        ms[M10] = nms00 * m_sinZ;
        ms[M11] = nms01 * m_sinZ + nms11 * cosZ;
        ms[M12] = nms02 * m_sinZ + nms12 * cosZ;
        return this;
    }

    /**
     * Set only the upper left 3x3 submatrix of this matrix to a rotation of <code>angleZ</code> radians about the Z axis, followed by a rotation
     * of <code>angleY</code> radians about the Y axis and followed by a rotation of <code>angleX</code> radians about the X axis.
     * 
     * @param angleZ
     *            the angle to rotate about Z
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @return this
     */
    public Matrix4d setRotationZYX(double angleZ, double angleY, double angleX) {
        double cosZ = Math.cos(angleZ);
        double sinZ = Math.sin(angleZ);
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        double m_sinZ = -sinZ;
        double m_sinY = -sinY;
        double m_sinX = -sinX;

        // rotateZ
        double nms00 = cosZ;
        double nms01 = sinZ;
        double nms10 = m_sinZ;
        double nms11 = cosZ;
        // rotateY
        double nms20 = nms00 * sinY;
        double nms21 = nms01 * sinY;
        double nms22 = cosY;
        ms[M00] = nms00 * cosY;
        ms[M01] = nms01 * cosY;
        ms[M02] = m_sinY;
        // rotateX
        ms[M10] = nms10 * cosX + nms20 * sinX;
        ms[M11] = nms11 * cosX + nms21 * sinX;
        ms[M12] = nms22 * sinX;
        ms[M20] = nms10 * m_sinX + nms20 * cosX;
        ms[M21] = nms11 * m_sinX + nms21 * cosX;
        ms[M22] = nms22 * cosX;
        return this;
    }

    /**
     * Set only the upper left 3x3 submatrix of this matrix to a rotation of <code>angleY</code> radians about the Y axis, followed by a rotation
     * of <code>angleX</code> radians about the X axis and followed by a rotation of <code>angleZ</code> radians about the Z axis.
     * 
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @param angleZ
     *            the angle to rotate about Z
     * @return this
     */
    public Matrix4d setRotationYXZ(double angleY, double angleX, double angleZ) {
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        double cosZ = Math.cos(angleZ);
        double sinZ = Math.sin(angleZ);
        double m_sinY = -sinY;
        double m_sinX = -sinX;
        double m_sinZ = -sinZ;

        // rotateY
        double nms00 = cosY;
        double nms02 = m_sinY;
        double nms20 = sinY;
        double nms22 = cosY;
        // rotateX
        double nms10 = nms20 * sinX;
        double nms11 = cosX;
        double nms12 = nms22 * sinX;
        ms[M20] = nms20 * cosX;
        ms[M21] = m_sinX;
        ms[M22] = nms22 * cosX;
        // rotateZ
        ms[M00] = nms00 * cosZ + nms10 * sinZ;
        ms[M01] = nms11 * sinZ;
        ms[M02] = nms02 * cosZ + nms12 * sinZ;
        ms[M10] = nms00 * m_sinZ + nms10 * cosZ;
        ms[M11] = nms11 * cosZ;
        ms[M12] = nms02 * m_sinZ + nms12 * cosZ;
        return this;
    }

    /**
     * Set this matrix to a rotation matrix which rotates the given radians about a given axis.
     * 
     * @param angle
     *          the angle in radians
     * @param axis
     *          the axis to rotate about
     * @return this
     */
    public Matrix4d rotation(double angle, Vector3d axis) {
        return rotation(angle, axis.x, axis.y, axis.z);
    }

    /**
     * Set this matrix to a rotation matrix which rotates the given radians about a given axis.
     * 
     * @param angle
     *          the angle in radians
     * @param axis
     *          the axis to rotate about
     * @return this
     */
    public Matrix4d rotation(double angle, Vector3f axis) {
        return rotation(angle, axis.x, axis.y, axis.z);
    }

    /**
     * Transform/multiply the given vector by this matrix and store the result in that vector.
     * 
     * @see Vector4d#mul(Matrix4d)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    public Vector4d transform(Vector4d v) {
        return v.mul(this);
    }

    /**
     * Transform/multiply the given vector by this matrix and store the result in <code>dest</code>.
     * 
     * @see Vector4d#mul(Matrix4d, Vector4d)
     * 
     * @param v
     *          the vector to transform
     * @param dest
     *          will contain the result
     * @return dest
     */
    public Vector4d transform(Vector4d v, Vector4d dest) {
        return v.mul(this, dest);
    }

    /**
     * Transform/multiply the given vector by this matrix, perform perspective divide and store the result in that vector.
     * 
     * @see Vector4d#mulProject(Matrix4d)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    public Vector4d transformProject(Vector4d v) {
        return v.mulProject(this);
    }

    /**
     * Transform/multiply the given vector by this matrix, perform perspective divide and store the result in <code>dest</code>.
     * 
     * @see Vector4d#mulProject(Matrix4d, Vector4d)
     * 
     * @param v
     *          the vector to transform
     * @param dest
     *          will contain the result
     * @return dest
     */
    public Vector4d transformProject(Vector4d v, Vector4d dest) {
        return v.mulProject(this, dest);
    }

    /**
     * Transform/multiply the given vector by this matrix, perform perspective divide and store the result in that vector.
     * <p>
     * This method uses <tt>w=1.0</tt> as the fourth vector component.
     * 
     * @see Vector3d#mulProject(Matrix4d)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    public Vector3d transformProject(Vector3d v) {
        return v.mulProject(this);
    }

    /**
     * Transform/multiply the given vector by this matrix, perform perspective divide and store the result in <code>dest</code>.
     * <p>
     * This method uses <tt>w=1.0</tt> as the fourth vector component.
     * 
     * @see Vector3d#mulProject(Matrix4d, Vector3d)
     * 
     * @param v
     *          the vector to transform
     * @param dest
     *          will contain the result
     * @return dest
     */
    public Vector3d transformProject(Vector3d v, Vector3d dest) {
        return v.mulProject(this, dest);
    }

    /**
     * Transform/multiply the given 3D-vector, as if it was a 4D-vector with w=1, by
     * this matrix and store the result in that vector.
     * <p>
     * The given 3D-vector is treated as a 4D-vector with its w-component being 1.0, so it
     * will represent a position/location in 3D-space rather than a direction. This method is therefore
     * not suited for perspective projection transformations as it will not save the
     * <tt>w</tt> component of the transformed vector.
     * For perspective projection use {@link #transform(Vector4d)} or
     * {@link #transformProject(Vector3d)} when perspective divide should be applied, too.
     * <p>
     * In order to store the result in another vector, use {@link #transformPosition(Vector3d, Vector3d)}.
     * 
     * @see #transformPosition(Vector3d, Vector3d)
     * @see #transform(Vector4d)
     * @see #transformProject(Vector3d)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    public Vector3d transformPosition(Vector3d v) {
        v.set(ms[M00] * v.x + ms[M10] * v.y + ms[M20] * v.z + ms[M30],
              ms[M01] * v.x + ms[M11] * v.y + ms[M21] * v.z + ms[M31],
              ms[M02] * v.x + ms[M12] * v.y + ms[M22] * v.z + ms[M32]);
        return v;
    }

    /**
     * Transform/multiply the given 3D-vector, as if it was a 4D-vector with w=1, by
     * this matrix and store the result in <code>dest</code>.
     * <p>
     * The given 3D-vector is treated as a 4D-vector with its w-component being 1.0, so it
     * will represent a position/location in 3D-space rather than a direction. This method is therefore
     * not suited for perspective projection transformations as it will not save the
     * <tt>w</tt> component of the transformed vector.
     * For perspective projection use {@link #transform(Vector4d, Vector4d)} or
     * {@link #transformProject(Vector3d, Vector3d)} when perspective divide should be applied, too.
     * <p>
     * In order to store the result in the same vector, use {@link #transformPosition(Vector3d)}.
     * 
     * @see #transformPosition(Vector3d)
     * @see #transform(Vector4d, Vector4d)
     * @see #transformProject(Vector3d, Vector3d)
     * 
     * @param v
     *          the vector to transform
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Vector3d transformPosition(Vector3d v, Vector3d dest) {
        dest.set(ms[M00] * v.x + ms[M10] * v.y + ms[M20] * v.z + ms[M30],
                 ms[M01] * v.x + ms[M11] * v.y + ms[M21] * v.z + ms[M31],
                 ms[M02] * v.x + ms[M12] * v.y + ms[M22] * v.z + ms[M32]);
        return dest;
    }

    /**
     * Transform/multiply the given 3D-vector, as if it was a 4D-vector with w=0, by
     * this matrix and store the result in that vector.
     * <p>
     * The given 3D-vector is treated as a 4D-vector with its w-component being <tt>0.0</tt>, so it
     * will represent a direction in 3D-space rather than a position. This method will therefore
     * not take the translation part of the matrix into account.
     * <p>
     * In order to store the result in another vector, use {@link #transformDirection(Vector3d, Vector3d)}.
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    public Vector3d transformDirection(Vector3d v) {
        v.set(ms[M00] * v.x + ms[M10] * v.y + ms[M20] * v.z,
              ms[M01] * v.x + ms[M11] * v.y + ms[M21] * v.z,
              ms[M02] * v.x + ms[M12] * v.y + ms[M22] * v.z);
        return v;
    }

    /**
     * Transform/multiply the given 3D-vector, as if it was a 4D-vector with w=0, by
     * this matrix and store the result in <code>dest</code>.
     * <p>
     * The given 3D-vector is treated as a 4D-vector with its w-component being <tt>0.0</tt>, so it
     * will represent a direction in 3D-space rather than a position. This method will therefore
     * not take the translation part of the matrix into account.
     * <p>
     * In order to store the result in the same vector, use {@link #transformDirection(Vector3d)}.
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Vector3d transformDirection(Vector3d v, Vector3d dest) {
        dest.set(ms[M00] * v.x + ms[M10] * v.y + ms[M20] * v.z,
                 ms[M01] * v.x + ms[M11] * v.y + ms[M21] * v.z,
                 ms[M02] * v.x + ms[M12] * v.y + ms[M22] * v.z);
        return dest;
    }

    /**
     * Transform/multiply the given 4D-vector by assuming that <code>this</code> matrix represents an {@link #isAffine() affine} transformation
     * (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>).
     * <p>
     * In order to store the result in another vector, use {@link #transformAffine(Vector4d, Vector4d)}.
     * 
     * @see #transformAffine(Vector4d, Vector4d)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @return v
     */
    public Vector4d transformAffine(Vector4d v) {
        v.set(ms[M00] * v.x + ms[M10] * v.y + ms[M20] * v.z + ms[M30] * v.w,
              ms[M01] * v.x + ms[M11] * v.y + ms[M21] * v.z + ms[M31] * v.w,
              ms[M02] * v.x + ms[M12] * v.y + ms[M22] * v.z + ms[M32] * v.w,
              v.w);
        return v;
    }

    /**
     * Transform/multiply the given 4D-vector by assuming that <code>this</code> matrix represents an {@link #isAffine() affine} transformation
     * (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>) and store the result in <code>dest</code>.
     * <p>
     * In order to store the result in the same vector, use {@link #transformAffine(Vector4d)}.
     * 
     * @see #transformAffine(Vector4d)
     * 
     * @param v
     *          the vector to transform and to hold the final result
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Vector4d transformAffine(Vector4d v, Vector4d dest) {
        dest.set(ms[M00] * v.x + ms[M10] * v.y + ms[M20] * v.z + ms[M30] * v.w,
                 ms[M01] * v.x + ms[M11] * v.y + ms[M21] * v.z + ms[M31] * v.w,
                 ms[M02] * v.x + ms[M12] * v.y + ms[M22] * v.z + ms[M32] * v.w,
                 v.w);
        return dest;
    }

    /**
     * Set the upper 3x3 matrix of this {@link Matrix4d} to the given {@link Matrix3d} and the rest to the identity.
     * 
     * @param mat
     *          the 3x3 matrix
     * @return this
     */
    public Matrix4d set3x3(Matrix3d mat) {
        ms[M00] = mat.ms[Matrix3d.M00];
        ms[M01] = mat.ms[Matrix3d.M01];
        ms[M02] = mat.ms[Matrix3d.M02];
        ms[M03] = 0.0;
        ms[M10] = mat.ms[Matrix3d.M10];
        ms[M11] = mat.ms[Matrix3d.M11];
        ms[M12] = mat.ms[Matrix3d.M12];
        ms[M13] = 0.0;
        ms[M20] = mat.ms[Matrix3d.M20];
        ms[M21] = mat.ms[Matrix3d.M21];
        ms[M22] = mat.ms[Matrix3d.M22];
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Apply scaling to the this matrix by scaling the base axes by the given <tt>xyz.x</tt>,
     * <tt>xyz.y</tt> and <tt>xyz.z</tt> factors, respectively and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>
     * , the scaling will be applied first!
     * 
     * @param xyz
     *            the factors of the x, y and z component, respectively
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d scale(Vector3d xyz, Matrix4d dest) {
        return scale(xyz.x, xyz.y, xyz.z, dest);
    }

    /**
     * Apply scaling to this matrix by scaling the base axes by the given <tt>xyz.x</tt>,
     * <tt>xyz.y</tt> and <tt>xyz.z</tt> factors, respectively.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * scaling will be applied first!
     * 
     * @param xyz
     *            the factors of the x, y and z component, respectively
     * @return this
     */
    public Matrix4d scale(Vector3d xyz) {
        return scale(xyz.x, xyz.y, xyz.z, this);
    }

    /**
     * Apply scaling to the this matrix by scaling the base axes by the given x,
     * y and z factors and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>
     * , the scaling will be applied first!
     * 
     * @param x
     *            the factor of the x component
     * @param y
     *            the factor of the y component
     * @param z
     *            the factor of the z component
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d scale(double x, double y, double z, Matrix4d dest) {
        // scale matrix elements:
        // ms[M00] = x, ms[M11] = y, ms[M22] = z
        // ms[M33] = 1
        // all others = 0
        dest.ms[M00] = ms[M00] * x;
        dest.ms[M01] = ms[M01] * x;
        dest.ms[M02] = ms[M02] * x;
        dest.ms[M03] = ms[M03] * x;
        dest.ms[M10] = ms[M10] * y;
        dest.ms[M11] = ms[M11] * y;
        dest.ms[M12] = ms[M12] * y;
        dest.ms[M13] = ms[M13] * y;
        dest.ms[M20] = ms[M20] * z;
        dest.ms[M21] = ms[M21] * z;
        dest.ms[M22] = ms[M22] * z;
        dest.ms[M23] = ms[M23] * z;
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Apply scaling to the this matrix by scaling the base axes by the given x,
     * y and z factors.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>
     * , the scaling will be applied first!
     * 
     * @param x
     *            the factor of the x component
     * @param y
     *            the factor of the y component
     * @param z
     *            the factor of the z component
     * @return this
     */
    public Matrix4d scale(double x, double y, double z) {
        return scale(x, y, z, this);
    }

    /**
     * Apply scaling to this matrix by uniformly scaling all base axes by the given xyz factor
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>
     * , the scaling will be applied first!
     * 
     * @see #scale(double, double, double, Matrix4d)
     * 
     * @param xyz
     *            the factor for all components
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d scale(double xyz, Matrix4d dest) {
        return scale(xyz, xyz, xyz, dest);
    }

    /**
     * Apply scaling to this matrix by uniformly scaling all base axes by the given xyz factor.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the scaling matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>
     * , the scaling will be applied first!
     * 
     * @see #scale(double, double, double)
     * 
     * @param xyz
     *            the factor for all components
     * @return this
     */
    public Matrix4d scale(double xyz) {
        return scale(xyz, xyz, xyz);
    }

    /**
     * Apply rotation to this matrix by rotating the given amount of radians
     * about the given axis specified as x, y and z components and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>
     * , the rotation will be applied first!
     * 
     * @param ang
     *            the angle is in radians
     * @param x
     *            the x component of the axis
     * @param y
     *            the y component of the axis
     * @param z
     *            the z component of the axis
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d rotate(double ang, double x, double y, double z, Matrix4d dest) {
        double s = Math.sin(ang);
        double c = Math.cos(ang);
        double C = 1.0 - c;

        // rotation matrix elements:
        // ms[M30], ms[M31], ms[M32], ms[M03], ms[M13], ms[M23] = 0
        // ms[M33] = 1
        double xx = x * x, xy = x * y, xz = x * z;
        double yy = y * y, yz = y * z;
        double zz = z * z;
        double rn00 = xx * C + c;
        double rn01 = xy * C + z * s;
        double rn02 = xz * C - y * s;
        double rn10 = xy * C - z * s;
        double rn11 = yy * C + c;
        double rn12 = yz * C + x * s;
        double rn20 = xz * C + y * s;
        double rn21 = yz * C - x * s;
        double rn22 = zz * C + c;

        // add temporaries for dependent values
        double nms00 = ms[M00] * rn00 + ms[M10] * rn01 + ms[M20] * rn02;
        double nms01 = ms[M01] * rn00 + ms[M11] * rn01 + ms[M21] * rn02;
        double nms02 = ms[M02] * rn00 + ms[M12] * rn01 + ms[M22] * rn02;
        double nms03 = ms[M03] * rn00 + ms[M13] * rn01 + ms[M23] * rn02;
        double nms10 = ms[M00] * rn10 + ms[M10] * rn11 + ms[M20] * rn12;
        double nms11 = ms[M01] * rn10 + ms[M11] * rn11 + ms[M21] * rn12;
        double nms12 = ms[M02] * rn10 + ms[M12] * rn11 + ms[M22] * rn12;
        double nms13 = ms[M03] * rn10 + ms[M13] * rn11 + ms[M23] * rn12;
        // set non-dependent values directly
        dest.ms[M20] = ms[M00] * rn20 + ms[M10] * rn21 + ms[M20] * rn22;
        dest.ms[M21] = ms[M01] * rn20 + ms[M11] * rn21 + ms[M21] * rn22;
        dest.ms[M22] = ms[M02] * rn20 + ms[M12] * rn21 + ms[M22] * rn22;
        dest.ms[M23] = ms[M03] * rn20 + ms[M13] * rn21 + ms[M23] * rn22;
        // set other values
        dest.ms[M00] = nms00;
        dest.ms[M01] = nms01;
        dest.ms[M02] = nms02;
        dest.ms[M03] = nms03;
        dest.ms[M10] = nms10;
        dest.ms[M11] = nms11;
        dest.ms[M12] = nms12;
        dest.ms[M13] = nms13;
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];

        return dest;
    }

    /**
     * Apply rotation to this matrix by rotating the given amount of radians
     * about the given axis specified as x, y and z components.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>
     * , the rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation matrix without post-multiplying the rotation
     * transformation, use {@link #rotation(double, double, double, double) rotation()}.
     * 
     * @see #rotation(double, double, double, double)
     *  
     * @param ang
     *            the angle is in radians
     * @param x
     *            the x component of the axis
     * @param y
     *            the y component of the axis
     * @param z
     *            the z component of the axis
     * @return this
     */
    public Matrix4d rotate(double ang, double x, double y, double z) {
        return rotate(ang, x, y, z, this);
    }

    /**
     * Apply a translation to this matrix by translating by the given number of
     * units in x, y and z.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>T</code> the translation
     * matrix, then the new matrix will be <code>M * T</code>. So when
     * transforming a vector <code>v</code> with the new matrix by using
     * <code>M * T * v</code>, the translation will be applied first!
     * <p>
     * In order to set the matrix to a translation transformation without post-multiplying
     * it, use {@link #translation(Vector3d)}.
     * 
     * @see #translation(Vector3d)
     * 
     * @param offset
     *          the number of units in x, y and z by which to translate
     * @return this
     */
    public Matrix4d translate(Vector3d offset) {
        return translate(offset.x, offset.y, offset.z);
    }

    /**
     * Apply a translation to this matrix by translating by the given number of
     * units in x, y and z and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>T</code> the translation
     * matrix, then the new matrix will be <code>M * T</code>. So when
     * transforming a vector <code>v</code> with the new matrix by using
     * <code>M * T * v</code>, the translation will be applied first!
     * <p>
     * In order to set the matrix to a translation transformation without post-multiplying
     * it, use {@link #translation(Vector3d)}.
     * 
     * @see #translation(Vector3d)
     * 
     * @param offset
     *          the number of units in x, y and z by which to translate
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d translate(Vector3d offset, Matrix4d dest) {
        return translate(offset.x, offset.y, offset.z, dest);
    }

    /**
     * Apply a translation to this matrix by translating by the given number of
     * units in x, y and z and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>T</code> the translation
     * matrix, then the new matrix will be <code>M * T</code>. So when
     * transforming a vector <code>v</code> with the new matrix by using
     * <code>M * T * v</code>, the translation will be applied first!
     * <p>
     * In order to set the matrix to a translation transformation without post-multiplying
     * it, use {@link #translation(double, double, double)}.
     * 
     * @see #translation(double, double, double)
     * 
     * @param x
     *          the offset to translate in x
     * @param y
     *          the offset to translate in y
     * @param z
     *          the offset to translate in z
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d translate(double x, double y, double z, Matrix4d dest) {
        // translation matrix elements:
        // ms[M00], ms[M11], ms[M22], ms[M33] = 1
        // ms[M30] = x, ms[M31] = y, ms[M32] = z
        // all others = 0
        dest.ms[M00] = ms[M00];
        dest.ms[M01] = ms[M01];
        dest.ms[M02] = ms[M02];
        dest.ms[M03] = ms[M03];
        dest.ms[M10] = ms[M10];
        dest.ms[M11] = ms[M11];
        dest.ms[M12] = ms[M12];
        dest.ms[M13] = ms[M13];
        dest.ms[M20] = ms[M20];
        dest.ms[M21] = ms[M21];
        dest.ms[M22] = ms[M22];
        dest.ms[M23] = ms[M23];
        dest.ms[M30] = ms[M00] * x + ms[M10] * y + ms[M20] * z + ms[M30];
        dest.ms[M31] = ms[M01] * x + ms[M11] * y + ms[M21] * z + ms[M31];
        dest.ms[M32] = ms[M02] * x + ms[M12] * y + ms[M22] * z + ms[M32];
        dest.ms[M33] = ms[M03] * x + ms[M13] * y + ms[M23] * z + ms[M33];
        return dest;
    }

    /**
     * Apply a translation to this matrix by translating by the given number of
     * units in x, y and z.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>T</code> the translation
     * matrix, then the new matrix will be <code>M * T</code>. So when
     * transforming a vector <code>v</code> with the new matrix by using
     * <code>M * T * v</code>, the translation will be applied first!
     * <p>
     * In order to set the matrix to a translation transformation without post-multiplying
     * it, use {@link #translation(double, double, double)}.
     * 
     * @see #translation(double, double, double)
     * 
     * @param x
     *          the offset to translate in x
     * @param y
     *          the offset to translate in y
     * @param z
     *          the offset to translate in z
     * @return this
     */
    public Matrix4d translate(double x, double y, double z) {
        Matrix4d c = this;
        // translation matrix elements:
        // ms[M00], ms[M11], ms[M22], ms[M33] = 1
        // ms[M30] = x, ms[M31] = y, ms[M32] = z
        // all others = 0
        c.ms[M30] = c.ms[M00] * x + c.ms[M10] * y + c.ms[M20] * z + c.ms[M30];
        c.ms[M31] = c.ms[M01] * x + c.ms[M11] * y + c.ms[M21] * z + c.ms[M31];
        c.ms[M32] = c.ms[M02] * x + c.ms[M12] * y + c.ms[M22] * z + c.ms[M32];
        c.ms[M33] = c.ms[M03] * x + c.ms[M13] * y + c.ms[M23] * z + c.ms[M33];
        return this;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(ms[M00]);
        out.writeDouble(ms[M01]);
        out.writeDouble(ms[M02]);
        out.writeDouble(ms[M03]);
        out.writeDouble(ms[M10]);
        out.writeDouble(ms[M11]);
        out.writeDouble(ms[M12]);
        out.writeDouble(ms[M13]);
        out.writeDouble(ms[M20]);
        out.writeDouble(ms[M21]);
        out.writeDouble(ms[M22]);
        out.writeDouble(ms[M23]);
        out.writeDouble(ms[M30]);
        out.writeDouble(ms[M31]);
        out.writeDouble(ms[M32]);
        out.writeDouble(ms[M33]);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        ms[M00] = in.readDouble();
        ms[M01] = in.readDouble();
        ms[M02] = in.readDouble();
        ms[M03] = in.readDouble();
        ms[M10] = in.readDouble();
        ms[M11] = in.readDouble();
        ms[M12] = in.readDouble();
        ms[M13] = in.readDouble();
        ms[M20] = in.readDouble();
        ms[M21] = in.readDouble();
        ms[M22] = in.readDouble();
        ms[M23] = in.readDouble();
        ms[M30] = in.readDouble();
        ms[M31] = in.readDouble();
        ms[M32] = in.readDouble();
        ms[M33] = in.readDouble();
    }

    /**
     * Apply rotation about the X axis to this matrix by rotating the given amount of radians 
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">http://en.wikipedia.org</a>
     * 
     * @param ang
     *            the angle in radians
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d rotateX(double ang, Matrix4d dest) {
        double sin, cos;
        if (ang == Math.PI || ang == -Math.PI) {
            cos = -1.0;
            sin = 0.0;
        } else if (ang == Math.PI * 0.5 || ang == -Math.PI * 1.5) {
            cos = 0.0;
            sin = 1.0;
        } else if (ang == -Math.PI * 0.5 || ang == Math.PI * 1.5) {
            cos = 0.0;
            sin = -1.0;
        } else {
            cos = Math.cos(ang);
            sin = Math.sin(ang);
        }
        double rn11 = cos;
        double rn12 = sin;
        double rn21 = -sin;
        double rn22 = cos;

        // add temporaries for dependent values
        double nms10 = ms[M10] * rn11 + ms[M20] * rn12;
        double nms11 = ms[M11] * rn11 + ms[M21] * rn12;
        double nms12 = ms[M12] * rn11 + ms[M22] * rn12;
        double nms13 = ms[M13] * rn11 + ms[M23] * rn12;
        // set non-dependent values directly
        dest.ms[M20] = ms[M10] * rn21 + ms[M20] * rn22;
        dest.ms[M21] = ms[M11] * rn21 + ms[M21] * rn22;
        dest.ms[M22] = ms[M12] * rn21 + ms[M22] * rn22;
        dest.ms[M23] = ms[M13] * rn21 + ms[M23] * rn22;
        // set other values
        dest.ms[M10] = nms10;
        dest.ms[M11] = nms11;
        dest.ms[M12] = nms12;
        dest.ms[M13] = nms13;
        dest.ms[M00] = ms[M00];
        dest.ms[M01] = ms[M01];
        dest.ms[M02] = ms[M02];
        dest.ms[M03] = ms[M03];
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];

        return dest;
    }

    /**
     * Apply rotation about the X axis to this matrix by rotating the given amount of radians.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">http://en.wikipedia.org</a>
     * 
     * @param ang
     *            the angle in radians
     * @return this
     */
    public Matrix4d rotateX(double ang) {
        return rotateX(ang, this);
    }

    /**
     * Apply rotation about the Y axis to this matrix by rotating the given amount of radians 
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">http://en.wikipedia.org</a>
     * 
     * @param ang
     *            the angle in radians
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d rotateY(double ang, Matrix4d dest) {
        double sin, cos;
        if (ang == Math.PI || ang == -Math.PI) {
            cos = -1.0;
            sin = 0.0;
        } else if (ang == Math.PI * 0.5 || ang == -Math.PI * 1.5) {
            cos = 0.0;
            sin = 1.0;
        } else if (ang == -Math.PI * 0.5 || ang == Math.PI * 1.5) {
            cos = 0.0;
            sin = -1.0;
        } else {
            cos = Math.cos(ang);
            sin = Math.sin(ang);
        }
        double rn00 = cos;
        double rn02 = -sin;
        double rn20 = sin;
        double rn22 = cos;

        // add temporaries for dependent values
        double nms00 = ms[M00] * rn00 + ms[M20] * rn02;
        double nms01 = ms[M01] * rn00 + ms[M21] * rn02;
        double nms02 = ms[M02] * rn00 + ms[M22] * rn02;
        double nms03 = ms[M03] * rn00 + ms[M23] * rn02;
        // set non-dependent values directly
        dest.ms[M20] = ms[M00] * rn20 + ms[M20] * rn22;
        dest.ms[M21] = ms[M01] * rn20 + ms[M21] * rn22;
        dest.ms[M22] = ms[M02] * rn20 + ms[M22] * rn22;
        dest.ms[M23] = ms[M03] * rn20 + ms[M23] * rn22;
        // set other values
        dest.ms[M00] = nms00;
        dest.ms[M01] = nms01;
        dest.ms[M02] = nms02;
        dest.ms[M03] = nms03;
        dest.ms[M10] = ms[M10];
        dest.ms[M11] = ms[M11];
        dest.ms[M12] = ms[M12];
        dest.ms[M13] = ms[M13];
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];

        return dest;
    }

    /**
     * Apply rotation about the Y axis to this matrix by rotating the given amount of radians.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">http://en.wikipedia.org</a>
     * 
     * @param ang
     *            the angle in radians
     * @return this
     */
    public Matrix4d rotateY(double ang) {
        return rotateY(ang, this);
    }

    /**
     * Apply rotation about the Z axis to this matrix by rotating the given amount of radians 
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">http://en.wikipedia.org</a>
     * 
     * @param ang
     *            the angle in radians
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d rotateZ(double ang, Matrix4d dest) {
        double sin, cos;
        if (ang == Math.PI || ang == -Math.PI) {
            cos = -1.0;
            sin = 0.0;
        } else if (ang == Math.PI * 0.5 || ang == -Math.PI * 1.5) {
            cos = 0.0;
            sin = 1.0;
        } else if (ang == -Math.PI * 0.5 || ang == Math.PI * 1.5) {
            cos = 0.0;
            sin = -1.0;
        } else {
            cos = Math.cos(ang);
            sin = Math.sin(ang);
        }
        double rn00 = cos;
        double rn01 = sin;
        double rn10 = -sin;
        double rn11 = cos;

        // add temporaries for dependent values
        double nms00 = ms[M00] * rn00 + ms[M10] * rn01;
        double nms01 = ms[M01] * rn00 + ms[M11] * rn01;
        double nms02 = ms[M02] * rn00 + ms[M12] * rn01;
        double nms03 = ms[M03] * rn00 + ms[M13] * rn01;
        // set non-dependent values directly
        dest.ms[M10] = ms[M00] * rn10 + ms[M10] * rn11;
        dest.ms[M11] = ms[M01] * rn10 + ms[M11] * rn11;
        dest.ms[M12] = ms[M02] * rn10 + ms[M12] * rn11;
        dest.ms[M13] = ms[M03] * rn10 + ms[M13] * rn11;
        // set other values
        dest.ms[M00] = nms00;
        dest.ms[M01] = nms01;
        dest.ms[M02] = nms02;
        dest.ms[M03] = nms03;
        dest.ms[M20] = ms[M20];
        dest.ms[M21] = ms[M21];
        dest.ms[M22] = ms[M22];
        dest.ms[M23] = ms[M23];
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Apply rotation about the Z axis to this matrix by rotating the given amount of radians.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">http://en.wikipedia.org</a>
     * 
     * @param ang
     *            the angle in radians
     * @return this
     */
    public Matrix4d rotateZ(double ang) {
        return rotateZ(ang, this);
    }

    /**
     * Apply rotation of <code>angleX</code> radians about the X axis, followed by a rotation of <code>angleY</code> radians about the Y axis and
     * followed by a rotation of <code>angleZ</code> radians about the Z axis.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>rotateX(angleX).rotateY(angleY).rotateZ(angleZ)</tt>
     * 
     * @param angleX
     *            the angle to rotate about X
     * @param angleY
     *            the angle to rotate about Y
     * @param angleZ
     *            the angle to rotate about Z
     * @return this
     */
    public Matrix4d rotateXYZ(double angleX, double angleY, double angleZ) {
        return rotateXYZ(angleX, angleY, angleZ, this);
    }

    /**
     * Apply rotation of <code>angleX</code> radians about the X axis, followed by a rotation of <code>angleY</code> radians about the Y axis and
     * followed by a rotation of <code>angleZ</code> radians about the Z axis and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>rotateX(angleX, dest).rotateY(angleY).rotateZ(angleZ)</tt>
     * 
     * @param angleX
     *            the angle to rotate about X
     * @param angleY
     *            the angle to rotate about Y
     * @param angleZ
     *            the angle to rotate about Z
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d rotateXYZ(double angleX, double angleY, double angleZ, Matrix4d dest) {
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double cosZ = Math.cos(angleZ);
        double sinZ = Math.sin(angleZ);
        double m_sinX = -sinX;
        double m_sinY = -sinY;
        double m_sinZ = -sinZ;

        // rotateX
        double nms10 = ms[M10] * cosX + ms[M20] * sinX;
        double nms11 = ms[M11] * cosX + ms[M21] * sinX;
        double nms12 = ms[M12] * cosX + ms[M22] * sinX;
        double nms13 = ms[M13] * cosX + ms[M23] * sinX;
        double nms20 = ms[M10] * m_sinX + ms[M20] * cosX;
        double nms21 = ms[M11] * m_sinX + ms[M21] * cosX;
        double nms22 = ms[M12] * m_sinX + ms[M22] * cosX;
        double nms23 = ms[M13] * m_sinX + ms[M23] * cosX;
        // rotateY
        double nms00 = ms[M00] * cosY + nms20 * m_sinY;
        double nms01 = ms[M01] * cosY + nms21 * m_sinY;
        double nms02 = ms[M02] * cosY + nms22 * m_sinY;
        double nms03 = ms[M03] * cosY + nms23 * m_sinY;
        dest.ms[M20] = ms[M00] * sinY + nms20 * cosY;
        dest.ms[M21] = ms[M01] * sinY + nms21 * cosY;
        dest.ms[M22] = ms[M02] * sinY + nms22 * cosY;
        dest.ms[M23] = ms[M03] * sinY + nms23 * cosY;
        // rotateZ
        dest.ms[M00] = nms00 * cosZ + nms10 * sinZ;
        dest.ms[M01] = nms01 * cosZ + nms11 * sinZ;
        dest.ms[M02] = nms02 * cosZ + nms12 * sinZ;
        dest.ms[M03] = nms03 * cosZ + nms13 * sinZ;
        dest.ms[M10] = nms00 * m_sinZ + nms10 * cosZ;
        dest.ms[M11] = nms01 * m_sinZ + nms11 * cosZ;
        dest.ms[M12] = nms02 * m_sinZ + nms12 * cosZ;
        dest.ms[M13] = nms03 * m_sinZ + nms13 * cosZ;
        // copy last column from 'this'
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Apply rotation of <code>angleX</code> radians about the X axis, followed by a rotation of <code>angleY</code> radians about the Y axis and
     * followed by a rotation of <code>angleZ</code> radians about the Z axis.
     * <p>
     * This method assumes that <code>this</code> matrix represents an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>rotateX(angleX).rotateY(angleY).rotateZ(angleZ)</tt>
     * 
     * @param angleX
     *            the angle to rotate about X
     * @param angleY
     *            the angle to rotate about Y
     * @param angleZ
     *            the angle to rotate about Z
     * @return this
     */
    public Matrix4d rotateAffineXYZ(double angleX, double angleY, double angleZ) {
        return rotateAffineXYZ(angleX, angleY, angleZ, this);
    }

    /**
     * Apply rotation of <code>angleX</code> radians about the X axis, followed by a rotation of <code>angleY</code> radians about the Y axis and
     * followed by a rotation of <code>angleZ</code> radians about the Z axis and store the result in <code>dest</code>.
     * <p>
     * This method assumes that <code>this</code> matrix represents an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * 
     * @param angleX
     *            the angle to rotate about X
     * @param angleY
     *            the angle to rotate about Y
     * @param angleZ
     *            the angle to rotate about Z
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d rotateAffineXYZ(double angleX, double angleY, double angleZ, Matrix4d dest) {
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double cosZ = Math.cos(angleZ);
        double sinZ = Math.sin(angleZ);
        double m_sinX = -sinX;
        double m_sinY = -sinY;
        double m_sinZ = -sinZ;

        // rotateX
        double nms10 = ms[M10] * cosX + ms[M20] * sinX;
        double nms11 = ms[M11] * cosX + ms[M21] * sinX;
        double nms12 = ms[M12] * cosX + ms[M22] * sinX;
        double nms20 = ms[M10] * m_sinX + ms[M20] * cosX;
        double nms21 = ms[M11] * m_sinX + ms[M21] * cosX;
        double nms22 = ms[M12] * m_sinX + ms[M22] * cosX;
        // rotateY
        double nms00 = ms[M00] * cosY + nms20 * m_sinY;
        double nms01 = ms[M01] * cosY + nms21 * m_sinY;
        double nms02 = ms[M02] * cosY + nms22 * m_sinY;
        dest.ms[M20] = ms[M00] * sinY + nms20 * cosY;
        dest.ms[M21] = ms[M01] * sinY + nms21 * cosY;
        dest.ms[M22] = ms[M02] * sinY + nms22 * cosY;
        dest.ms[M23] = 0.0;
        // rotateZ
        dest.ms[M00] = nms00 * cosZ + nms10 * sinZ;
        dest.ms[M01] = nms01 * cosZ + nms11 * sinZ;
        dest.ms[M02] = nms02 * cosZ + nms12 * sinZ;
        dest.ms[M03] = 0.0;
        dest.ms[M10] = nms00 * m_sinZ + nms10 * cosZ;
        dest.ms[M11] = nms01 * m_sinZ + nms11 * cosZ;
        dest.ms[M12] = nms02 * m_sinZ + nms12 * cosZ;
        dest.ms[M13] = 0.0;
        // copy last column from 'this'
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Apply rotation of <code>angleZ</code> radians about the Z axis, followed by a rotation of <code>angleY</code> radians about the Y axis and
     * followed by a rotation of <code>angleX</code> radians about the X axis.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>rotateZ(angleZ).rotateY(angleY).rotateX(angleX)</tt>
     * 
     * @param angleZ
     *            the angle to rotate about Z
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @return this
     */
    public Matrix4d rotateZYX(double angleZ, double angleY, double angleX) {
        return rotateZYX(angleZ, angleY, angleX, this);
    }

    /**
     * Apply rotation of <code>angleZ</code> radians about the Z axis, followed by a rotation of <code>angleY</code> radians about the Y axis and
     * followed by a rotation of <code>angleX</code> radians about the X axis and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>rotateZ(angleZ, dest).rotateY(angleY).rotateX(angleX)</tt>
     * 
     * @param angleZ
     *            the angle to rotate about Z
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d rotateZYX(double angleZ, double angleY, double angleX, Matrix4d dest) {
        double cosZ = Math.cos(angleZ);
        double sinZ = Math.sin(angleZ);
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        double m_sinZ = -sinZ;
        double m_sinY = -sinY;
        double m_sinX = -sinX;

        // rotateZ
        double nms00 = ms[M00] * cosZ + ms[M10] * sinZ;
        double nms01 = ms[M01] * cosZ + ms[M11] * sinZ;
        double nms02 = ms[M02] * cosZ + ms[M12] * sinZ;
        double nms03 = ms[M03] * cosZ + ms[M13] * sinZ;
        double nms10 = ms[M00] * m_sinZ + ms[M10] * cosZ;
        double nms11 = ms[M01] * m_sinZ + ms[M11] * cosZ;
        double nms12 = ms[M02] * m_sinZ + ms[M12] * cosZ;
        double nms13 = ms[M03] * m_sinZ + ms[M13] * cosZ;
        // rotateY
        double nms20 = nms00 * sinY + ms[M20] * cosY;
        double nms21 = nms01 * sinY + ms[M21] * cosY;
        double nms22 = nms02 * sinY + ms[M22] * cosY;
        double nms23 = nms03 * sinY + ms[M23] * cosY;
        dest.ms[M00] = nms00 * cosY + ms[M20] * m_sinY;
        dest.ms[M01] = nms01 * cosY + ms[M21] * m_sinY;
        dest.ms[M02] = nms02 * cosY + ms[M22] * m_sinY;
        dest.ms[M03] = nms03 * cosY + ms[M23] * m_sinY;
        // rotateX
        dest.ms[M10] = nms10 * cosX + nms20 * sinX;
        dest.ms[M11] = nms11 * cosX + nms21 * sinX;
        dest.ms[M12] = nms12 * cosX + nms22 * sinX;
        dest.ms[M13] = nms13 * cosX + nms23 * sinX;
        dest.ms[M20] = nms10 * m_sinX + nms20 * cosX;
        dest.ms[M21] = nms11 * m_sinX + nms21 * cosX;
        dest.ms[M22] = nms12 * m_sinX + nms22 * cosX;
        dest.ms[M23] = nms13 * m_sinX + nms23 * cosX;
        // copy last column from 'this'
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Apply rotation of <code>angleZ</code> radians about the Z axis, followed by a rotation of <code>angleY</code> radians about the Y axis and
     * followed by a rotation of <code>angleX</code> radians about the X axis.
     * <p>
     * This method assumes that <code>this</code> matrix represents an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * 
     * @param angleZ
     *            the angle to rotate about Z
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @return this
     */
    public Matrix4d rotateAffineZYX(double angleZ, double angleY, double angleX) {
        return rotateAffineZYX(angleZ, angleY, angleX, this);
    }

    /**
     * Apply rotation of <code>angleZ</code> radians about the Z axis, followed by a rotation of <code>angleY</code> radians about the Y axis and
     * followed by a rotation of <code>angleX</code> radians about the X axis and store the result in <code>dest</code>.
     * <p>
     * This method assumes that <code>this</code> matrix represents an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * 
     * @param angleZ
     *            the angle to rotate about Z
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d rotateAffineZYX(double angleZ, double angleY, double angleX, Matrix4d dest) {
        double cosZ = Math.cos(angleZ);
        double sinZ = Math.sin(angleZ);
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        double m_sinZ = -sinZ;
        double m_sinY = -sinY;
        double m_sinX = -sinX;

        // rotateZ
        double nms00 = ms[M00] * cosZ + ms[M10] * sinZ;
        double nms01 = ms[M01] * cosZ + ms[M11] * sinZ;
        double nms02 = ms[M02] * cosZ + ms[M12] * sinZ;
        double nms10 = ms[M00] * m_sinZ + ms[M10] * cosZ;
        double nms11 = ms[M01] * m_sinZ + ms[M11] * cosZ;
        double nms12 = ms[M02] * m_sinZ + ms[M12] * cosZ;
        // rotateY
        double nms20 = nms00 * sinY + ms[M20] * cosY;
        double nms21 = nms01 * sinY + ms[M21] * cosY;
        double nms22 = nms02 * sinY + ms[M22] * cosY;
        dest.ms[M00] = nms00 * cosY + ms[M20] * m_sinY;
        dest.ms[M01] = nms01 * cosY + ms[M21] * m_sinY;
        dest.ms[M02] = nms02 * cosY + ms[M22] * m_sinY;
        dest.ms[M03] = 0.0;
        // rotateX
        dest.ms[M10] = nms10 * cosX + nms20 * sinX;
        dest.ms[M11] = nms11 * cosX + nms21 * sinX;
        dest.ms[M12] = nms12 * cosX + nms22 * sinX;
        dest.ms[M13] = 0.0;
        dest.ms[M20] = nms10 * m_sinX + nms20 * cosX;
        dest.ms[M21] = nms11 * m_sinX + nms21 * cosX;
        dest.ms[M22] = nms12 * m_sinX + nms22 * cosX;
        dest.ms[M23] = 0.0;
        // copy last column from 'this'
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Apply rotation of <code>angleY</code> radians about the Y axis, followed by a rotation of <code>angleX</code> radians about the X axis and
     * followed by a rotation of <code>angleZ</code> radians about the Z axis.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>rotateY(angleY).rotateX(angleX).rotateZ(angleZ)</tt>
     * 
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @param angleZ
     *            the angle to rotate about Z
     * @return this
     */
    public Matrix4d rotateYXZ(double angleY, double angleX, double angleZ) {
        return rotateYXZ(angleY, angleX, angleZ, this);
    }

    /**
     * Apply rotation of <code>angleY</code> radians about the Y axis, followed by a rotation of <code>angleX</code> radians about the X axis and
     * followed by a rotation of <code>angleZ</code> radians about the Z axis and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * This method is equivalent to calling: <tt>rotateY(angleY, dest).rotateX(angleX).rotateZ(angleZ)</tt>
     * 
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @param angleZ
     *            the angle to rotate about Z
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d rotateYXZ(double angleY, double angleX, double angleZ, Matrix4d dest) {
        double cosY =  Math.cos(angleY);
        double sinY =  Math.sin(angleY);
        double cosX =  Math.cos(angleX);
        double sinX =  Math.sin(angleX);
        double cosZ =  Math.cos(angleZ);
        double sinZ =  Math.sin(angleZ);
        double m_sinY = -sinY;
        double m_sinX = -sinX;
        double m_sinZ = -sinZ;

        // rotateY
        double nms20 = ms[M00] * sinY + ms[M20] * cosY;
        double nms21 = ms[M01] * sinY + ms[M21] * cosY;
        double nms22 = ms[M02] * sinY + ms[M22] * cosY;
        double nms23 = ms[M03] * sinY + ms[M23] * cosY;
        double nms00 = ms[M00] * cosY + ms[M20] * m_sinY;
        double nms01 = ms[M01] * cosY + ms[M21] * m_sinY;
        double nms02 = ms[M02] * cosY + ms[M22] * m_sinY;
        double nms03 = ms[M03] * cosY + ms[M23] * m_sinY;
        // rotateX
        double nms10 = ms[M10] * cosX + nms20 * sinX;
        double nms11 = ms[M11] * cosX + nms21 * sinX;
        double nms12 = ms[M12] * cosX + nms22 * sinX;
        double nms13 = ms[M13] * cosX + nms23 * sinX;
        dest.ms[M20] = ms[M10] * m_sinX + nms20 * cosX;
        dest.ms[M21] = ms[M11] * m_sinX + nms21 * cosX;
        dest.ms[M22] = ms[M12] * m_sinX + nms22 * cosX;
        dest.ms[M23] = ms[M13] * m_sinX + nms23 * cosX;
        // rotateZ
        dest.ms[M00] = nms00 * cosZ + nms10 * sinZ;
        dest.ms[M01] = nms01 * cosZ + nms11 * sinZ;
        dest.ms[M02] = nms02 * cosZ + nms12 * sinZ;
        dest.ms[M03] = nms03 * cosZ + nms13 * sinZ;
        dest.ms[M10] = nms00 * m_sinZ + nms10 * cosZ;
        dest.ms[M11] = nms01 * m_sinZ + nms11 * cosZ;
        dest.ms[M12] = nms02 * m_sinZ + nms12 * cosZ;
        dest.ms[M13] = nms03 * m_sinZ + nms13 * cosZ;
        // copy last column from 'this'
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Apply rotation of <code>angleY</code> radians about the Y axis, followed by a rotation of <code>angleX</code> radians about the X axis and
     * followed by a rotation of <code>angleZ</code> radians about the Z axis.
     * <p>
     * This method assumes that <code>this</code> matrix represents an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * 
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @param angleZ
     *            the angle to rotate about Z
     * @return this
     */
    public Matrix4d rotateAffineYXZ(double angleY, double angleX, double angleZ) {
        return rotateAffineYXZ(angleY, angleX, angleZ, this);
    }

    /**
     * Apply rotation of <code>angleY</code> radians about the Y axis, followed by a rotation of <code>angleX</code> radians about the X axis and
     * followed by a rotation of <code>angleZ</code> radians about the Z axis and store the result in <code>dest</code>.
     * <p>
     * This method assumes that <code>this</code> matrix represents an {@link #isAffine() affine} transformation (i.e. its last row is equal to <tt>(0, 0, 0, 1)</tt>)
     * and can be used to speed up matrix multiplication if the matrix only represents affine transformations, such as translation, rotation, scaling and shearing (in any combination).
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * 
     * @param angleY
     *            the angle to rotate about Y
     * @param angleX
     *            the angle to rotate about X
     * @param angleZ
     *            the angle to rotate about Z
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d rotateAffineYXZ(double angleY, double angleX, double angleZ, Matrix4d dest) {
        double cosY =  Math.cos(angleY);
        double sinY =  Math.sin(angleY);
        double cosX =  Math.cos(angleX);
        double sinX =  Math.sin(angleX);
        double cosZ =  Math.cos(angleZ);
        double sinZ =  Math.sin(angleZ);
        double m_sinY = -sinY;
        double m_sinX = -sinX;
        double m_sinZ = -sinZ;

        // rotateY
        double nms20 = ms[M00] * sinY + ms[M20] * cosY;
        double nms21 = ms[M01] * sinY + ms[M21] * cosY;
        double nms22 = ms[M02] * sinY + ms[M22] * cosY;
        double nms00 = ms[M00] * cosY + ms[M20] * m_sinY;
        double nms01 = ms[M01] * cosY + ms[M21] * m_sinY;
        double nms02 = ms[M02] * cosY + ms[M22] * m_sinY;
        // rotateX
        double nms10 = ms[M10] * cosX + nms20 * sinX;
        double nms11 = ms[M11] * cosX + nms21 * sinX;
        double nms12 = ms[M12] * cosX + nms22 * sinX;
        dest.ms[M20] = ms[M10] * m_sinX + nms20 * cosX;
        dest.ms[M21] = ms[M11] * m_sinX + nms21 * cosX;
        dest.ms[M22] = ms[M12] * m_sinX + nms22 * cosX;
        dest.ms[M23] = 0.0;
        // rotateZ
        dest.ms[M00] = nms00 * cosZ + nms10 * sinZ;
        dest.ms[M01] = nms01 * cosZ + nms11 * sinZ;
        dest.ms[M02] = nms02 * cosZ + nms12 * sinZ;
        dest.ms[M03] = 0.0;
        dest.ms[M10] = nms00 * m_sinZ + nms10 * cosZ;
        dest.ms[M11] = nms01 * m_sinZ + nms11 * cosZ;
        dest.ms[M12] = nms02 * m_sinZ + nms12 * cosZ;
        dest.ms[M13] = 0.0;
        // copy last column from 'this'
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Set this matrix to a rotation transformation using the given {@link AxisAngle4f}.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional rotation.
     * <p>
     * In order to apply the rotation transformation to an existing transformation,
     * use {@link #rotate(AxisAngle4f) rotate()} instead.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle">http://en.wikipedia.org</a>
     *
     * @see #rotate(AxisAngle4f)
     * 
     * @param angleAxis
     *          the {@link AxisAngle4f} (needs to be {@link AxisAngle4f#normalize() normalized})
     * @return this
     */
    public Matrix4d rotation(AxisAngle4f angleAxis) {
        return rotation(angleAxis.angle, angleAxis.x, angleAxis.y, angleAxis.z);
    }

    /**
     * Set this matrix to a rotation transformation using the given {@link AxisAngle4d}.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional rotation.
     * <p>
     * In order to apply the rotation transformation to an existing transformation,
     * use {@link #rotate(AxisAngle4d) rotate()} instead.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle">http://en.wikipedia.org</a>
     *
     * @see #rotate(AxisAngle4d)
     * 
     * @param angleAxis
     *          the {@link AxisAngle4d} (needs to be {@link AxisAngle4d#normalize() normalized})
     * @return this
     */
    public Matrix4d rotation(AxisAngle4d angleAxis) {
        return rotation(angleAxis.angle, angleAxis.x, angleAxis.y, angleAxis.z);
    }

    /**
     * Set this matrix to the rotation transformation of the given {@link Quaterniond}.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional rotation.
     * <p>
     * In order to apply the rotation transformation to an existing transformation,
     * use {@link #rotate(Quaterniond) rotate()} instead.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion">http://en.wikipedia.org</a>
     * 
     * @see #rotate(Quaterniond)
     * 
     * @param quat
     *          the {@link Quaterniond}
     * @return this
     */
    public Matrix4d rotation(Quaterniond quat) {
        double dqx = quat.x + quat.x;
        double dqy = quat.y + quat.y;
        double dqz = quat.z + quat.z;
        double q00 = dqx * quat.x;
        double q11 = dqy * quat.y;
        double q22 = dqz * quat.z;
        double q01 = dqx * quat.y;
        double q02 = dqx * quat.z;
        double q03 = dqx * quat.w;
        double q12 = dqy * quat.z;
        double q13 = dqy * quat.w;
        double q23 = dqz * quat.w;

        ms[M00] = 1.0 - q11 - q22;
        ms[M01] = q01 + q23;
        ms[M02] = q02 - q13;
        ms[M03] = 0.0;
        ms[M10] = q01 - q23;
        ms[M11] = 1.0 - q22 - q00;
        ms[M12] = q12 + q03;
        ms[M13] = 0.0;
        ms[M20] = q02 + q13;
        ms[M21] = q12 - q03;
        ms[M22] = 1.0 - q11 - q00;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;

        return this;
    }

    /**
     * Set this matrix to the rotation transformation of the given {@link Quaternionf}.
     * <p>
     * The resulting matrix can be multiplied against another transformation
     * matrix to obtain an additional rotation.
     * <p>
     * In order to apply the rotation transformation to an existing transformation,
     * use {@link #rotate(Quaternionf) rotate()} instead.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion">http://en.wikipedia.org</a>
     * 
     * @see #rotate(Quaternionf)
     * 
     * @param quat
     *          the {@link Quaternionf}
     * @return this
     */
    public Matrix4d rotation(Quaternionf quat) {
        double dqx = quat.x + quat.x;
        double dqy = quat.y + quat.y;
        double dqz = quat.z + quat.z;
        double q00 = dqx * quat.x;
        double q11 = dqy * quat.y;
        double q22 = dqz * quat.z;
        double q01 = dqx * quat.y;
        double q02 = dqx * quat.z;
        double q03 = dqx * quat.w;
        double q12 = dqy * quat.z;
        double q13 = dqy * quat.w;
        double q23 = dqz * quat.w;

        ms[M00] = 1.0 - q11 - q22;
        ms[M01] = q01 + q23;
        ms[M02] = q02 - q13;
        ms[M03] = 0.0;
        ms[M10] = q01 - q23;
        ms[M11] = 1.0 - q22 - q00;
        ms[M12] = q12 + q03;
        ms[M13] = 0.0;
        ms[M20] = q02 + q13;
        ms[M21] = q12 - q03;
        ms[M22] = 1.0 - q11 - q00;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;

        return this;
    }

    /**
     * Set <code>this</code> matrix to <tt>T * R * S</tt>, where <tt>T</tt> is a translation by the given <tt>(tx, ty, tz)</tt>,
     * <tt>R</tt> is a rotation transformation specified by the quaternion <tt>(qx, qy, qz, qw)</tt>, and <tt>S</tt> is a scaling transformation
     * which scales the three axes x, y and z by <tt>(sx, sy, sz)</tt>.
     * <p>
     * When transforming a vector by the resulting matrix the scaling transformation will be applied first, then the rotation and
     * at last the translation.
     * <p>
     * This method is equivalent to calling: <tt>translation(tx, ty, tz).rotate(quat).scale(sx, sy, sz)</tt>
     * 
     * @see #translation(double, double, double)
     * @see #rotate(Quaterniond)
     * @see #scale(double, double, double)
     * 
     * @param tx
     *          the number of units by which to translate the x-component
     * @param ty
     *          the number of units by which to translate the y-component
     * @param tz
     *          the number of units by which to translate the z-component
     * @param qx
     *          the x-coordinate of the vector part of the quaternion
     * @param qy
     *          the y-coordinate of the vector part of the quaternion
     * @param qz
     *          the z-coordinate of the vector part of the quaternion
     * @param qw
     *          the scalar part of the quaternion
     * @param sx
     *          the scaling factor for the x-axis
     * @param sy
     *          the scaling factor for the y-axis
     * @param sz
     *          the scaling factor for the z-axis
     * @return this
     */
    public Matrix4d translationRotateScale(double tx, double ty, double tz, 
                                           double qx, double qy, double qz, double qw, 
                                           double sx, double sy, double sz) {
        double dqx = qx + qx, dqy = qy + qy, dqz = qz + qz;
        double q00 = dqx * qx;
        double q11 = dqy * qy;
        double q22 = dqz * qz;
        double q01 = dqx * qy;
        double q02 = dqx * qz;
        double q03 = dqx * qw;
        double q12 = dqy * qz;
        double q13 = dqy * qw;
        double q23 = dqz * qw;
        ms[M00] = sx - (q11 + q22) * sx;
        ms[M01] = (q01 + q23) * sx;
        ms[M02] = (q02 - q13) * sx;
        ms[M03] = 0.0;
        ms[M10] = (q01 - q23) * sy;
        ms[M11] = sy - (q22 + q00) * sy;
        ms[M12] = (q12 + q03) * sy;
        ms[M13] = 0.0;
        ms[M20] = (q02 + q13) * sz;
        ms[M21] = (q12 - q03) * sz;
        ms[M22] = sz - (q11 + q00) * sz;
        ms[M23] = 0.0;
        ms[M30] = tx;
        ms[M31] = ty;
        ms[M32] = tz;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set <code>this</code> matrix to <tt>T * R * S</tt>, where <tt>T</tt> is the given <code>translation</code>,
     * <tt>R</tt> is a rotation transformation specified by the given quaternion, and <tt>S</tt> is a scaling transformation
     * which scales the axes by <code>scale</code>.
     * <p>
     * When transforming a vector by the resulting matrix the scaling transformation will be applied first, then the rotation and
     * at last the translation.
     * <p>
     * This method is equivalent to calling: <tt>translation(translation).rotate(quat).scale(scale)</tt>
     * 
     * @see #translation(Vector3f)
     * @see #rotate(Quaternionf)
     * 
     * @param translation
     *          the translation
     * @param quat
     *          the quaternion representing a rotation
     * @param scale
     *          the scaling factors
     * @return this
     */
    public Matrix4d translationRotateScale(Vector3f translation, 
                                           Quaternionf quat, 
                                           Vector3f scale) {
        return translationRotateScale(translation.x, translation.y, translation.z, quat.x, quat.y, quat.z, quat.w, scale.x, scale.y, scale.z);
    }

    /**
     * Set <code>this</code> matrix to <tt>T * R * S</tt>, where <tt>T</tt> is the given <code>translation</code>,
     * <tt>R</tt> is a rotation transformation specified by the given quaternion, and <tt>S</tt> is a scaling transformation
     * which scales the axes by <code>scale</code>.
     * <p>
     * When transforming a vector by the resulting matrix the scaling transformation will be applied first, then the rotation and
     * at last the translation.
     * <p>
     * This method is equivalent to calling: <tt>translation(translation).rotate(quat).scale(scale)</tt>
     * 
     * @see #translation(Vector3d)
     * @see #rotate(Quaterniond)
     * 
     * @param translation
     *          the translation
     * @param quat
     *          the quaternion representing a rotation
     * @param scale
     *          the scaling factors
     * @return this
     */
    public Matrix4d translationRotateScale(Vector3d translation, 
                                           Quaterniond quat, 
                                           Vector3d scale) {
        return translationRotateScale(translation.x, translation.y, translation.z, quat.x, quat.y, quat.z, quat.w, scale.x, scale.y, scale.z);
    }

    /**
     * Set <code>this</code> matrix to <tt>T * R</tt>, where <tt>T</tt> is a translation by the given <tt>(tx, ty, tz)</tt> and
     * <tt>R</tt> is a rotation transformation specified by the given quaternion.
     * <p>
     * When transforming a vector by the resulting matrix the rotation transformation will be applied first and then the translation.
     * <p>
     * This method is equivalent to calling: <tt>translation(tx, ty, tz).rotate(quat)</tt>
     * 
     * @see #translation(double, double, double)
     * @see #rotate(Quaterniond)
     * 
     * @param tx
     *          the number of units by which to translate the x-component
     * @param ty
     *          the number of units by which to translate the y-component
     * @param tz
     *          the number of units by which to translate the z-component
     * @param quat
     *          the quaternion representing a rotation
     * @return this
     */
    public Matrix4d translationRotate(double tx, double ty, double tz, Quaterniond quat) {
        double dqx = quat.x + quat.x, dqy = quat.y + quat.y, dqz = quat.z + quat.z;
        double q00 = dqx * quat.x;
        double q11 = dqy * quat.y;
        double q22 = dqz * quat.z;
        double q01 = dqx * quat.y;
        double q02 = dqx * quat.z;
        double q03 = dqx * quat.w;
        double q12 = dqy * quat.z;
        double q13 = dqy * quat.w;
        double q23 = dqz * quat.w;
        ms[M00] = 1.0 - (q11 + q22);
        ms[M01] = q01 + q23;
        ms[M02] = q02 - q13;
        ms[M03] = 0.0;
        ms[M10] = q01 - q23;
        ms[M11] = 1.0 - (q22 + q00);
        ms[M12] = q12 + q03;
        ms[M13] = 0.0;
        ms[M20] = q02 + q13;
        ms[M21] = q12 - q03;
        ms[M22] = 1.0 - (q11 + q00);
        ms[M23] = 0.0;
        ms[M30] = tx;
        ms[M31] = ty;
        ms[M32] = tz;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Apply the rotation transformation of the given {@link Quaterniond} to this matrix and store
     * the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>Q</code> the rotation matrix obtained from the given quaternion,
     * then the new matrix will be <code>M * Q</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * Q * v</code>,
     * the quaternion rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(Quaterniond)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion">http://en.wikipedia.org</a>
     * 
     * @see #rotation(Quaterniond)
     * 
     * @param quat
     *          the {@link Quaterniond}
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d rotate(Quaterniond quat, Matrix4d dest) {
        double dqx = quat.x + quat.x;
        double dqy = quat.y + quat.y;
        double dqz = quat.z + quat.z;
        double q00 = dqx * quat.x;
        double q11 = dqy * quat.y;
        double q22 = dqz * quat.z;
        double q01 = dqx * quat.y;
        double q02 = dqx * quat.z;
        double q03 = dqx * quat.w;
        double q12 = dqy * quat.z;
        double q13 = dqy * quat.w;
        double q23 = dqz * quat.w;

        double rn00 = 1.0 - q11 - q22;
        double rn01 = q01 + q23;
        double rn02 = q02 - q13;
        double rn10 = q01 - q23;
        double rn11 = 1.0 - q22 - q00;
        double rn12 = q12 + q03;
        double rn20 = q02 + q13;
        double rn21 = q12 - q03;
        double rn22 = 1.0 - q11 - q00;

        double nms00 = ms[M00] * rn00 + ms[M10] * rn01 + ms[M20] * rn02;
        double nms01 = ms[M01] * rn00 + ms[M11] * rn01 + ms[M21] * rn02;
        double nms02 = ms[M02] * rn00 + ms[M12] * rn01 + ms[M22] * rn02;
        double nms03 = ms[M03] * rn00 + ms[M13] * rn01 + ms[M23] * rn02;
        double nms10 = ms[M00] * rn10 + ms[M10] * rn11 + ms[M20] * rn12;
        double nms11 = ms[M01] * rn10 + ms[M11] * rn11 + ms[M21] * rn12;
        double nms12 = ms[M02] * rn10 + ms[M12] * rn11 + ms[M22] * rn12;
        double nms13 = ms[M03] * rn10 + ms[M13] * rn11 + ms[M23] * rn12;
        dest.ms[M20] = ms[M00] * rn20 + ms[M10] * rn21 + ms[M20] * rn22;
        dest.ms[M21] = ms[M01] * rn20 + ms[M11] * rn21 + ms[M21] * rn22;
        dest.ms[M22] = ms[M02] * rn20 + ms[M12] * rn21 + ms[M22] * rn22;
        dest.ms[M23] = ms[M03] * rn20 + ms[M13] * rn21 + ms[M23] * rn22;
        dest.ms[M00] = nms00;
        dest.ms[M01] = nms01;
        dest.ms[M02] = nms02;
        dest.ms[M03] = nms03;
        dest.ms[M10] = nms10;
        dest.ms[M11] = nms11;
        dest.ms[M12] = nms12;
        dest.ms[M13] = nms13;
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];

        return dest;
    }

    /**
     * Apply the rotation transformation of the given {@link Quaternionf} to this matrix and store
     * the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>Q</code> the rotation matrix obtained from the given quaternion,
     * then the new matrix will be <code>M * Q</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * Q * v</code>,
     * the quaternion rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(Quaternionf)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion">http://en.wikipedia.org</a>
     * 
     * @see #rotation(Quaternionf)
     * 
     * @param quat
     *          the {@link Quaternionf}
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d rotate(Quaternionf quat, Matrix4d dest) {
        double dqx = quat.x + quat.x;
        double dqy = quat.y + quat.y;
        double dqz = quat.z + quat.z;
        double q00 = dqx * quat.x;
        double q11 = dqy * quat.y;
        double q22 = dqz * quat.z;
        double q01 = dqx * quat.y;
        double q02 = dqx * quat.z;
        double q03 = dqx * quat.w;
        double q12 = dqy * quat.z;
        double q13 = dqy * quat.w;
        double q23 = dqz * quat.w;

        double rn00 = 1.0 - q11 - q22;
        double rn01 = q01 + q23;
        double rn02 = q02 - q13;
        double rn10 = q01 - q23;
        double rn11 = 1.0 - q22 - q00;
        double rn12 = q12 + q03;
        double rn20 = q02 + q13;
        double rn21 = q12 - q03;
        double rn22 = 1.0 - q11 - q00;

        double nms00 = ms[M00] * rn00 + ms[M10] * rn01 + ms[M20] * rn02;
        double nms01 = ms[M01] * rn00 + ms[M11] * rn01 + ms[M21] * rn02;
        double nms02 = ms[M02] * rn00 + ms[M12] * rn01 + ms[M22] * rn02;
        double nms03 = ms[M03] * rn00 + ms[M13] * rn01 + ms[M23] * rn02;
        double nms10 = ms[M00] * rn10 + ms[M10] * rn11 + ms[M20] * rn12;
        double nms11 = ms[M01] * rn10 + ms[M11] * rn11 + ms[M21] * rn12;
        double nms12 = ms[M02] * rn10 + ms[M12] * rn11 + ms[M22] * rn12;
        double nms13 = ms[M03] * rn10 + ms[M13] * rn11 + ms[M23] * rn12;
        dest.ms[M20] = ms[M00] * rn20 + ms[M10] * rn21 + ms[M20] * rn22;
        dest.ms[M21] = ms[M01] * rn20 + ms[M11] * rn21 + ms[M21] * rn22;
        dest.ms[M22] = ms[M02] * rn20 + ms[M12] * rn21 + ms[M22] * rn22;
        dest.ms[M23] = ms[M03] * rn20 + ms[M13] * rn21 + ms[M23] * rn22;
        dest.ms[M00] = nms00;
        dest.ms[M01] = nms01;
        dest.ms[M02] = nms02;
        dest.ms[M03] = nms03;
        dest.ms[M10] = nms10;
        dest.ms[M11] = nms11;
        dest.ms[M12] = nms12;
        dest.ms[M13] = nms13;
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];

        return dest;
    }

    /**
     * Apply the rotation transformation of the given {@link Quaterniond} to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>Q</code> the rotation matrix obtained from the given quaternion,
     * then the new matrix will be <code>M * Q</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * Q * v</code>,
     * the quaternion rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(Quaterniond)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion">http://en.wikipedia.org</a>
     * 
     * @see #rotation(Quaterniond)
     * 
     * @param quat
     *          the {@link Quaterniond}
     * @return this
     */
    public Matrix4d rotate(Quaterniond quat) {
        return rotate(quat, this);
    }

    /**
     * Apply the rotation transformation of the given {@link Quaternionf} to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>Q</code> the rotation matrix obtained from the given quaternion,
     * then the new matrix will be <code>M * Q</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * Q * v</code>,
     * the quaternion rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(Quaternionf)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion">http://en.wikipedia.org</a>
     * 
     * @see #rotation(Quaternionf)
     * 
     * @param quat
     *          the {@link Quaternionf}
     * @return this
     */
    public Matrix4d rotate(Quaternionf quat) {
        return rotate(quat, this);
    }

    /**
     * Apply a rotation transformation, rotating about the given {@link AxisAngle4f}, to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>A</code> the rotation matrix obtained from the given {@link AxisAngle4f},
     * then the new matrix will be <code>M * A</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * A * v</code>,
     * the {@link AxisAngle4f} rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(AxisAngle4f)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle">http://en.wikipedia.org</a>
     * 
     * @see #rotate(double, double, double, double)
     * @see #rotation(AxisAngle4f)
     * 
     * @param axisAngle
     *          the {@link AxisAngle4f} (needs to be {@link AxisAngle4f#normalize() normalized})
     * @return this
     */
    public Matrix4d rotate(AxisAngle4f axisAngle) {
        return rotate(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z);
    }

    /**
     * Apply a rotation transformation, rotating about the given {@link AxisAngle4f} and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>A</code> the rotation matrix obtained from the given {@link AxisAngle4f},
     * then the new matrix will be <code>M * A</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * A * v</code>,
     * the {@link AxisAngle4f} rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(AxisAngle4f)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle">http://en.wikipedia.org</a>
     * 
     * @see #rotate(double, double, double, double)
     * @see #rotation(AxisAngle4f)
     * 
     * @param axisAngle
     *          the {@link AxisAngle4f} (needs to be {@link AxisAngle4f#normalize() normalized})
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d rotate(AxisAngle4f axisAngle, Matrix4d dest) {
        return rotate(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z, dest);
    }

    /**
     * Apply a rotation transformation, rotating about the given {@link AxisAngle4d}, to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>A</code> the rotation matrix obtained from the given {@link AxisAngle4d},
     * then the new matrix will be <code>M * A</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * A * v</code>,
     * the {@link AxisAngle4d} rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(AxisAngle4d)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle">http://en.wikipedia.org</a>
     * 
     * @see #rotate(double, double, double, double)
     * @see #rotation(AxisAngle4d)
     * 
     * @param axisAngle
     *          the {@link AxisAngle4d} (needs to be {@link AxisAngle4d#normalize() normalized})
     * @return this
     */
    public Matrix4d rotate(AxisAngle4d axisAngle) {
        return rotate(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z);
    }

    /**
     * Apply a rotation transformation, rotating about the given {@link AxisAngle4d} and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>A</code> the rotation matrix obtained from the given {@link AxisAngle4d},
     * then the new matrix will be <code>M * A</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * A * v</code>,
     * the {@link AxisAngle4d} rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(AxisAngle4d)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle">http://en.wikipedia.org</a>
     * 
     * @see #rotate(double, double, double, double)
     * @see #rotation(AxisAngle4d)
     * 
     * @param axisAngle
     *          the {@link AxisAngle4d} (needs to be {@link AxisAngle4d#normalize() normalized})
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d rotate(AxisAngle4d axisAngle, Matrix4d dest) {
        return rotate(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z, dest);
    }

    /**
     * Apply a rotation transformation, rotating the given radians about the specified axis, to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>A</code> the rotation matrix obtained from the given angle and axis,
     * then the new matrix will be <code>M * A</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * A * v</code>,
     * the axis-angle rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(double, Vector3d)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle">http://en.wikipedia.org</a>
     * 
     * @see #rotate(double, double, double, double)
     * @see #rotation(double, Vector3d)
     * 
     * @param angle
     *          the angle in radians
     * @param axis
     *          the rotation axis (needs to be {@link Vector3d#normalize() normalized})
     * @return this
     */
    public Matrix4d rotate(double angle, Vector3d axis) {
        return rotate(angle, axis.x, axis.y, axis.z);
    }

    /**
     * Apply a rotation transformation, rotating the given radians about the specified axis and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>A</code> the rotation matrix obtained from the given angle and axis,
     * then the new matrix will be <code>M * A</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * A * v</code>,
     * the axis-angle rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(double, Vector3d)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle">http://en.wikipedia.org</a>
     * 
     * @see #rotate(double, double, double, double)
     * @see #rotation(double, Vector3d)
     * 
     * @param angle
     *          the angle in radians
     * @param axis
     *          the rotation axis (needs to be {@link Vector3d#normalize() normalized})
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d rotate(double angle, Vector3d axis, Matrix4d dest) {
        return rotate(angle, axis.x, axis.y, axis.z, dest);
    }

    /**
     * Apply a rotation transformation, rotating the given radians about the specified axis, to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>A</code> the rotation matrix obtained from the given angle and axis,
     * then the new matrix will be <code>M * A</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * A * v</code>,
     * the axis-angle rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(double, Vector3f)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle">http://en.wikipedia.org</a>
     * 
     * @see #rotate(double, double, double, double)
     * @see #rotation(double, Vector3f)
     * 
     * @param angle
     *          the angle in radians
     * @param axis
     *          the rotation axis (needs to be {@link Vector3f#normalize() normalized})
     * @return this
     */
    public Matrix4d rotate(double angle, Vector3f axis) {
        return rotate(angle, axis.x, axis.y, axis.z);
    }

    /**
     * Apply a rotation transformation, rotating the given radians about the specified axis and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>A</code> the rotation matrix obtained from the given angle and axis,
     * then the new matrix will be <code>M * A</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * A * v</code>,
     * the axis-angle rotation will be applied first!
     * <p>
     * In order to set the matrix to a rotation transformation without post-multiplying,
     * use {@link #rotation(double, Vector3f)}.
     * <p>
     * Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Axis_and_angle">http://en.wikipedia.org</a>
     * 
     * @see #rotate(double, double, double, double)
     * @see #rotation(double, Vector3f)
     * 
     * @param angle
     *          the angle in radians
     * @param axis
     *          the rotation axis (needs to be {@link Vector3f#normalize() normalized})
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d rotate(double angle, Vector3f axis, Matrix4d dest) {
        return rotate(angle, axis.x, axis.y, axis.z, dest);
    }

    /**
     * Get the row at the given <code>row</code> index, starting with <code>0</code>.
     * 
     * @param row
     *          the row index in <tt>[0..3]</tt>
     * @param dest
     *          will hold the row components
     * @return the passed in destination
     * @throws IndexOutOfBoundsException if <code>row</code> is not in <tt>[0..3]</tt>
     */
    public Vector4d getRow(int row, Vector4d dest) throws IndexOutOfBoundsException {
        switch (row) {
        case 0:
            dest.x = ms[M00];
            dest.y = ms[M10];
            dest.z = ms[M20];
            dest.w = ms[M30];
            break;
        case 1:
            dest.x = ms[M01];
            dest.y = ms[M11];
            dest.z = ms[M21];
            dest.w = ms[M31];
            break;
        case 2:
            dest.x = ms[M02];
            dest.y = ms[M12];
            dest.z = ms[M22];
            dest.w = ms[M32];
            break;
        case 3:
            dest.x = ms[M03];
            dest.y = ms[M13];
            dest.z = ms[M23];
            dest.w = ms[M33];
            break;
        default:
            throw new IndexOutOfBoundsException();
        }
        
        return dest;
    }

    /**
     * Get the column at the given <code>column</code> index, starting with <code>0</code>.
     * 
     * @param column
     *          the column index in <tt>[0..3]</tt>
     * @param dest
     *          will hold the column components
     * @return the passed in destination
     * @throws IndexOutOfBoundsException if <code>column</code> is not in <tt>[0..3]</tt>
     */
    public Vector4d getColumn(int column, Vector4d dest) throws IndexOutOfBoundsException {
        switch (column) {
        case 0:
            dest.x = ms[M00];
            dest.y = ms[M01];
            dest.z = ms[M02];
            dest.w = ms[M03];
            break;
        case 1:
            dest.x = ms[M10];
            dest.y = ms[M11];
            dest.z = ms[M12];
            dest.w = ms[M13];
            break;
        case 2:
            dest.x = ms[M20];
            dest.y = ms[M21];
            dest.z = ms[M22];
            dest.w = ms[M23];
            break;
        case 3:
            dest.x = ms[M30];
            dest.y = ms[M31];
            dest.z = ms[M32];
            dest.w = ms[M32];
            break;
        default:
            throw new IndexOutOfBoundsException();
        }
        
        return dest;
    }

    /**
     * Compute a normal matrix from the upper left 3x3 submatrix of <code>this</code>
     * and store it into the upper left 3x3 submatrix of <code>this</code>.
     * All other values of <code>this</code> will be set to {@link #identity() identity}.
     * <p>
     * The normal matrix of <tt>m</tt> is the transpose of the inverse of <tt>m</tt>.
     * <p>
     * Please note that, if <code>this</code> is an orthogonal matrix or a matrix whose columns are orthogonal vectors, 
     * then this method <i>need not</i> be invoked, since in that case <code>this</code> itself is its normal matrix.
     * In that case, use {@link #set3x3(Matrix4d)} to set a given Matrix4f to only the upper left 3x3 submatrix
     * of this matrix.
     * 
     * @see #set3x3(Matrix4d)
     * 
     * @return this
     */
    public Matrix4d normal() {
        return normal(this);
    }

    /**
     * Compute a normal matrix from the upper left 3x3 submatrix of <code>this</code>
     * and store it into the upper left 3x3 submatrix of <code>dest</code>.
     * All other values of <code>dest</code> will be set to {@link #identity() identity}.
     * <p>
     * The normal matrix of <tt>m</tt> is the transpose of the inverse of <tt>m</tt>.
     * <p>
     * Please note that, if <code>this</code> is an orthogonal matrix or a matrix whose columns are orthogonal vectors, 
     * then this method <i>need not</i> be invoked, since in that case <code>this</code> itself is its normal matrix.
     * In that case, use {@link #set3x3(Matrix4d)} to set a given Matrix4d to only the upper left 3x3 submatrix
     * of a given matrix.
     * 
     * @see #set3x3(Matrix4d)
     * 
     * @param dest
     *             will hold the result
     * @return dest
     */
    public Matrix4d normal(Matrix4d dest) {
        double det = determinant3x3();
        double s = 1.0 / det;
        /* Invert and transpose in one go */
        dest.set((ms[M11] * ms[M22] - ms[M21] * ms[M12]) * s,
                 (ms[M20] * ms[M12] - ms[M10] * ms[M22]) * s,
                 (ms[M10] * ms[M21] - ms[M20] * ms[M11]) * s,
                 0.0,
                 (ms[M21] * ms[M02] - ms[M01] * ms[M22]) * s,
                 (ms[M00] * ms[M22] - ms[M20] * ms[M02]) * s,
                 (ms[M20] * ms[M01] - ms[M00] * ms[M21]) * s,
                 0.0,
                 (ms[M01] * ms[M12] - ms[M11] * ms[M02]) * s,
                 (ms[M10] * ms[M02] - ms[M00] * ms[M12]) * s,
                 (ms[M00] * ms[M11] - ms[M10] * ms[M01]) * s,
                 0.0,
                 0.0, 0.0, 0.0, 1.0);
        return dest;
    }

    /**
     * Compute a normal matrix from the upper left 3x3 submatrix of <code>this</code>
     * and store it into <code>dest</code>.
     * <p>
     * The normal matrix of <tt>m</tt> is the transpose of the inverse of <tt>m</tt>.
     * <p>
     * Please note that, if <code>this</code> is an orthogonal matrix or a matrix whose columns are orthogonal vectors, 
     * then this method <i>need not</i> be invoked, since in that case <code>this</code> itself is its normal matrix.
     * In that case, use {@link Matrix3d#set(Matrix4d)} to set a given Matrix3d to only the upper left 3x3 submatrix
     * of this matrix.
     * 
     * @see Matrix3d#set(Matrix4d)
     * @see #get3x3(Matrix3d)
     * 
     * @param dest
     *             will hold the result
     * @return dest
     */
    public Matrix3d normal(Matrix3d dest) {
        double det = determinant3x3();
        double s = 1.0 / det;
        /* Invert and transpose in one go */
        dest.ms[Matrix3d.M00] = (ms[M11] * ms[M22] - ms[M21] * ms[M12]) * s;
        dest.ms[Matrix3d.M01] = (ms[M20] * ms[M12] - ms[M10] * ms[M22]) * s;
        dest.ms[Matrix3d.M02] = (ms[M10] * ms[M21] - ms[M20] * ms[M11]) * s;
        dest.ms[Matrix3d.M10] = (ms[M21] * ms[M02] - ms[M01] * ms[M22]) * s;
        dest.ms[Matrix3d.M11] = (ms[M00] * ms[M22] - ms[M20] * ms[M02]) * s;
        dest.ms[Matrix3d.M12] = (ms[M20] * ms[M01] - ms[M00] * ms[M21]) * s;
        dest.ms[Matrix3d.M20] = (ms[M01] * ms[M12] - ms[M11] * ms[M02]) * s;
        dest.ms[Matrix3d.M21] = (ms[M10] * ms[M02] - ms[M00] * ms[M12]) * s;
        dest.ms[Matrix3d.M22] = (ms[M00] * ms[M11] - ms[M10] * ms[M01]) * s;
        return dest;
    }

    /**
     * Normalize the upper left 3x3 submatrix of this matrix.
     * <p>
     * The resulting matrix will map unit vectors to unit vectors, though a pair of orthogonal input unit
     * vectors need not be mapped to a pair of orthogonal output vectors if the original matrix was not orthogonal itself
     * (i.e. had <i>skewing</i>).
     * 
     * @return this
     */
    public Matrix4d normalize3x3() {
        return normalize3x3(this);
    }

    /**
     * Normalize the upper left 3x3 submatrix of this matrix and store the result in <code>dest</code>.
     * <p>
     * The resulting matrix will map unit vectors to unit vectors, though a pair of orthogonal input unit
     * vectors need not be mapped to a pair of orthogonal output vectors if the original matrix was not orthogonal itself
     * (i.e. had <i>skewing</i>).
     * 
     * @param dest
     *             will hold the result
     * @return dest
     */
    public Matrix4d normalize3x3(Matrix4d dest) {
        double invXlen = 1.0 / Math.sqrt(ms[M00] * ms[M00] + ms[M01] * ms[M01] + ms[M02] * ms[M02]);
        double invYlen = 1.0 / Math.sqrt(ms[M10] * ms[M10] + ms[M11] * ms[M11] + ms[M12] * ms[M12]);
        double invZlen = 1.0 / Math.sqrt(ms[M20] * ms[M20] + ms[M21] * ms[M21] + ms[M22] * ms[M22]);
        dest.ms[M00] = ms[M00] * invXlen; dest.ms[M01] = ms[M01] * invXlen; dest.ms[M02] = ms[M02] * invXlen;
        dest.ms[M10] = ms[M10] * invYlen; dest.ms[M11] = ms[M11] * invYlen; dest.ms[M12] = ms[M12] * invYlen;
        dest.ms[M20] = ms[M20] * invZlen; dest.ms[M21] = ms[M21] * invZlen; dest.ms[M22] = ms[M22] * invZlen;
        return dest;
    }

    /**
     * Normalize the upper left 3x3 submatrix of this matrix and store the result in <code>dest</code>.
     * <p>
     * The resulting matrix will map unit vectors to unit vectors, though a pair of orthogonal input unit
     * vectors need not be mapped to a pair of orthogonal output vectors if the original matrix was not orthogonal itself
     * (i.e. had <i>skewing</i>).
     * 
     * @param dest
     *             will hold the result
     * @return dest
     */
    public Matrix3d normalize3x3(Matrix3d dest) {
        double invXlen = 1.0 / Math.sqrt(ms[M00] * ms[M00] + ms[M01] * ms[M01] + ms[M02] * ms[M02]);
        double invYlen = 1.0 / Math.sqrt(ms[M10] * ms[M10] + ms[M11] * ms[M11] + ms[M12] * ms[M12]);
        double invZlen = 1.0 / Math.sqrt(ms[M20] * ms[M20] + ms[M21] * ms[M21] + ms[M22] * ms[M22]);
        dest.ms[Matrix3d.M00] = ms[M00] * invXlen; dest.ms[Matrix3d.M01] = ms[M01] * invXlen; dest.ms[Matrix3d.M02] = ms[M02] * invXlen;
        dest.ms[Matrix3d.M10] = ms[M10] * invYlen; dest.ms[Matrix3d.M11] = ms[M11] * invYlen; dest.ms[Matrix3d.M12] = ms[M12] * invYlen;
        dest.ms[Matrix3d.M20] = ms[M20] * invZlen; dest.ms[Matrix3d.M21] = ms[M21] * invZlen; dest.ms[Matrix3d.M22] = ms[M22] * invZlen;
        return dest;
    }

    /**
     * Unproject the given window coordinates <tt>(winX, winY, winZ)</tt> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
     * and then transforms those NDC coordinates by the inverse of <code>this</code> matrix.  
     * <p>
     * The depth range of <tt>winZ</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
     * <p>
     * As a necessary computation step for unprojecting, this method computes the inverse of <code>this</code> matrix.
     * In order to avoid computing the matrix inverse with every invocation, the inverse of <code>this</code> matrix can be built
     * once outside using {@link #invert(Matrix4d)} and then the method {@link #unprojectInv(double, double, double, int[], Vector4d) unprojectInv()} can be invoked on it.
     * 
     * @see #unprojectInv(double, double, double, int[], Vector4d)
     * @see #invert(Matrix4d)
     * 
     * @param winX
     *          the x-coordinate in window coordinates (pixels)
     * @param winY
     *          the y-coordinate in window coordinates (pixels)
     * @param winZ
     *          the z-coordinate, which is the depth value in <tt>[0..1]</tt>
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          will hold the unprojected position
     * @return dest
     */
    public Vector4d unproject(double winX, double winY, double winZ, int[] viewport, Vector4d dest) {
        double a = ms[M00] * ms[M11] - ms[M01] * ms[M10];
        double b = ms[M00] * ms[M12] - ms[M02] * ms[M10];
        double c = ms[M00] * ms[M13] - ms[M03] * ms[M10];
        double d = ms[M01] * ms[M12] - ms[M02] * ms[M11];
        double e = ms[M01] * ms[M13] - ms[M03] * ms[M11];
        double f = ms[M02] * ms[M13] - ms[M03] * ms[M12];
        double g = ms[M20] * ms[M31] - ms[M21] * ms[M30];
        double h = ms[M20] * ms[M32] - ms[M22] * ms[M30];
        double i = ms[M20] * ms[M33] - ms[M23] * ms[M30];
        double j = ms[M21] * ms[M32] - ms[M22] * ms[M31];
        double k = ms[M21] * ms[M33] - ms[M23] * ms[M31];
        double l = ms[M22] * ms[M33] - ms[M23] * ms[M32];
        double det = a * l - b * k + c * j + d * i - e * h + f * g;
        det = 1.0 / det;
        double ims00 = ( ms[M11] * l - ms[M12] * k + ms[M13] * j) * det;
        double ims01 = (-ms[M01] * l + ms[M02] * k - ms[M03] * j) * det;
        double ims02 = ( ms[M31] * f - ms[M32] * e + ms[M33] * d) * det;
        double ims03 = (-ms[M21] * f + ms[M22] * e - ms[M23] * d) * det;
        double ims10 = (-ms[M10] * l + ms[M12] * i - ms[M13] * h) * det;
        double ims11 = ( ms[M00] * l - ms[M02] * i + ms[M03] * h) * det;
        double ims12 = (-ms[M30] * f + ms[M32] * c - ms[M33] * b) * det;
        double ims13 = ( ms[M20] * f - ms[M22] * c + ms[M23] * b) * det;
        double ims20 = ( ms[M10] * k - ms[M11] * i + ms[M13] * g) * det;
        double ims21 = (-ms[M00] * k + ms[M01] * i - ms[M03] * g) * det;
        double ims22 = ( ms[M30] * e - ms[M31] * c + ms[M33] * a) * det;
        double ims23 = (-ms[M20] * e + ms[M21] * c - ms[M23] * a) * det;
        double ims30 = (-ms[M10] * j + ms[M11] * h - ms[M12] * g) * det;
        double ims31 = ( ms[M00] * j - ms[M01] * h + ms[M02] * g) * det;
        double ims32 = (-ms[M30] * d + ms[M31] * b - ms[M32] * a) * det;
        double ims33 = ( ms[M20] * d - ms[M21] * b + ms[M22] * a) * det;
        double ndcX = (winX-viewport[0])/viewport[2]*2.0-1.0;
        double ndcY = (winY-viewport[1])/viewport[3]*2.0-1.0;
        double ndcZ = winZ+winZ-1.0;
        dest.x = ims00 * ndcX + ims10 * ndcY + ims20 * ndcZ + ims30;
        dest.y = ims01 * ndcX + ims11 * ndcY + ims21 * ndcZ + ims31;
        dest.z = ims02 * ndcX + ims12 * ndcY + ims22 * ndcZ + ims32;
        dest.w = ims03 * ndcX + ims13 * ndcY + ims23 * ndcZ + ims33;
        dest.div(dest.w);
        return dest;
    }

    /**
     * Unproject the given window coordinates <tt>(winX, winY, winZ)</tt> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
     * and then transforms those NDC coordinates by the inverse of <code>this</code> matrix.  
     * <p>
     * The depth range of <tt>winZ</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
     * <p>
     * As a necessary computation step for unprojecting, this method computes the inverse of <code>this</code> matrix.
     * In order to avoid computing the matrix inverse with every invocation, the inverse of <code>this</code> matrix can be built
     * once outside using {@link #invert(Matrix4d)} and then the method {@link #unprojectInv(double, double, double, int[], Vector3d) unprojectInv()} can be invoked on it.
     * 
     * @see #unprojectInv(double, double, double, int[], Vector3d)
     * @see #invert(Matrix4d)
     * 
     * @param winX
     *          the x-coordinate in window coordinates (pixels)
     * @param winY
     *          the y-coordinate in window coordinates (pixels)
     * @param winZ
     *          the z-coordinate, which is the depth value in <tt>[0..1]</tt>
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          will hold the unprojected position
     * @return dest
     */
    public Vector3d unproject(double winX, double winY, double winZ, int[] viewport, Vector3d dest) {
        double a = ms[M00] * ms[M11] - ms[M01] * ms[M10];
        double b = ms[M00] * ms[M12] - ms[M02] * ms[M10];
        double c = ms[M00] * ms[M13] - ms[M03] * ms[M10];
        double d = ms[M01] * ms[M12] - ms[M02] * ms[M11];
        double e = ms[M01] * ms[M13] - ms[M03] * ms[M11];
        double f = ms[M02] * ms[M13] - ms[M03] * ms[M12];
        double g = ms[M20] * ms[M31] - ms[M21] * ms[M30];
        double h = ms[M20] * ms[M32] - ms[M22] * ms[M30];
        double i = ms[M20] * ms[M33] - ms[M23] * ms[M30];
        double j = ms[M21] * ms[M32] - ms[M22] * ms[M31];
        double k = ms[M21] * ms[M33] - ms[M23] * ms[M31];
        double l = ms[M22] * ms[M33] - ms[M23] * ms[M32];
        double det = a * l - b * k + c * j + d * i - e * h + f * g;
        det = 1.0 / det;
        double ims00 = ( ms[M11] * l - ms[M12] * k + ms[M13] * j) * det;
        double ims01 = (-ms[M01] * l + ms[M02] * k - ms[M03] * j) * det;
        double ims02 = ( ms[M31] * f - ms[M32] * e + ms[M33] * d) * det;
        double ims03 = (-ms[M21] * f + ms[M22] * e - ms[M23] * d) * det;
        double ims10 = (-ms[M10] * l + ms[M12] * i - ms[M13] * h) * det;
        double ims11 = ( ms[M00] * l - ms[M02] * i + ms[M03] * h) * det;
        double ims12 = (-ms[M30] * f + ms[M32] * c - ms[M33] * b) * det;
        double ims13 = ( ms[M20] * f - ms[M22] * c + ms[M23] * b) * det;
        double ims20 = ( ms[M10] * k - ms[M11] * i + ms[M13] * g) * det;
        double ims21 = (-ms[M00] * k + ms[M01] * i - ms[M03] * g) * det;
        double ims22 = ( ms[M30] * e - ms[M31] * c + ms[M33] * a) * det;
        double ims23 = (-ms[M20] * e + ms[M21] * c - ms[M23] * a) * det;
        double ims30 = (-ms[M10] * j + ms[M11] * h - ms[M12] * g) * det;
        double ims31 = ( ms[M00] * j - ms[M01] * h + ms[M02] * g) * det;
        double ims32 = (-ms[M30] * d + ms[M31] * b - ms[M32] * a) * det;
        double ims33 = ( ms[M20] * d - ms[M21] * b + ms[M22] * a) * det;
        double ndcX = (winX-viewport[0])/viewport[2]*2.0-1.0;
        double ndcY = (winY-viewport[1])/viewport[3]*2.0-1.0;
        double ndcZ = winZ+winZ-1.0;
        dest.x = ims00 * ndcX + ims10 * ndcY + ims20 * ndcZ + ims30;
        dest.y = ims01 * ndcX + ims11 * ndcY + ims21 * ndcZ + ims31;
        dest.z = ims02 * ndcX + ims12 * ndcY + ims22 * ndcZ + ims32;
        double w = ims03 * ndcX + ims13 * ndcY + ims23 * ndcZ + ims33;
        dest.div(w);
        return dest;
    }

    /**
     * Unproject the given window coordinates <code>winCoords</code> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
     * and then transforms those NDC coordinates by the inverse of <code>this</code> matrix.  
     * <p>
     * The depth range of <tt>winCoords.z</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
     * <p>
     * As a necessary computation step for unprojecting, this method computes the inverse of <code>this</code> matrix.
     * In order to avoid computing the matrix inverse with every invocation, the inverse of <code>this</code> matrix can be built
     * once outside using {@link #invert(Matrix4d)} and then the method {@link #unprojectInv(double, double, double, int[], Vector4d) unprojectInv()} can be invoked on it.
     * 
     * @see #unprojectInv(double, double, double, int[], Vector4d)
     * @see #unproject(double, double, double, int[], Vector4d)
     * @see #invert(Matrix4d)
     * 
     * @param winCoords
     *          the window coordinates to unproject
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          will hold the unprojected position
     * @return dest
     */
    public Vector4d unproject(Vector3d winCoords, int[] viewport, Vector4d dest) {
        return unproject(winCoords.x, winCoords.y, winCoords.z, viewport, dest);
    }

    /**
     * Unproject the given window coordinates <code>winCoords</code> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
     * and then transforms those NDC coordinates by the inverse of <code>this</code> matrix.  
     * <p>
     * The depth range of <tt>winCoords.z</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
     * <p>
     * As a necessary computation step for unprojecting, this method computes the inverse of <code>this</code> matrix.
     * In order to avoid computing the matrix inverse with every invocation, the inverse of <code>this</code> matrix can be built
     * once outside using {@link #invert(Matrix4d)} and then the method {@link #unprojectInv(double, double, double, int[], Vector4d) unprojectInv()} can be invoked on it.
     * 
     * @see #unprojectInv(double, double, double, int[], Vector4d)
     * @see #unproject(double, double, double, int[], Vector4d)
     * @see #invert(Matrix4d)
     * 
     * @param winCoords
     *          the window coordinates to unproject
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          will hold the unprojected position
     * @return dest
     */
    public Vector3d unproject(Vector3d winCoords, int[] viewport, Vector3d dest) {
        return unproject(winCoords.x, winCoords.y, winCoords.z, viewport, dest);
    }

    /**
     * Unproject the given window coordinates <code>winCoords</code> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method differs from {@link #unproject(Vector3d, int[], Vector4d) unproject()} 
     * in that it assumes that <code>this</code> is already the inverse matrix of the original projection matrix.
     * It exists to avoid recomputing the matrix inverse with every invocation.
     * <p>
     * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
     * and then transforms those NDC coordinates by <code>this</code> matrix.  
     * <p>
     * The depth range of <tt>winCoords.z</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
     * 
     * @see #unproject(Vector3d, int[], Vector4d)
     * 
     * @param winCoords
     *          the window coordinates to unproject
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          will hold the unprojected position
     * @return dest
     */
    public Vector4d unprojectInv(Vector3d winCoords, int[] viewport, Vector4d dest) {
        return unprojectInv(winCoords.x, winCoords.y, winCoords.z, viewport, dest);
    }

    /**
     * Unproject the given window coordinates <tt>(winX, winY, winZ)</tt> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method differs from {@link #unproject(double, double, double, int[], Vector4d) unproject()} 
     * in that it assumes that <code>this</code> is already the inverse matrix of the original projection matrix.
     * It exists to avoid recomputing the matrix inverse with every invocation.
     * <p>
     * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
     * and then transforms those NDC coordinates by <code>this</code> matrix.  
     * <p>
     * The depth range of <tt>winZ</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
     * 
     * @see #unproject(double, double, double, int[], Vector4d)
     * 
     * @param winX
     *          the x-coordinate in window coordinates (pixels)
     * @param winY
     *          the y-coordinate in window coordinates (pixels)
     * @param winZ
     *          the z-coordinate, which is the depth value in <tt>[0..1]</tt>
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          will hold the unprojected position
     * @return dest
     */
    public Vector4d unprojectInv(double winX, double winY, double winZ, int[] viewport, Vector4d dest) {
        double ndcX = (winX-viewport[0])/viewport[2]*2.0-1.0;
        double ndcY = (winY-viewport[1])/viewport[3]*2.0-1.0;
        double ndcZ = winZ+winZ-1.0;
        dest.x = ms[M00] * ndcX + ms[M10] * ndcY + ms[M20] * ndcZ + ms[M30];
        dest.y = ms[M01] * ndcX + ms[M11] * ndcY + ms[M21] * ndcZ + ms[M31];
        dest.z = ms[M02] * ndcX + ms[M12] * ndcY + ms[M22] * ndcZ + ms[M32];
        dest.w = ms[M03] * ndcX + ms[M13] * ndcY + ms[M23] * ndcZ + ms[M33];
        dest.div(dest.w);
        return dest;
    }

    /**
     * Unproject the given window coordinates <code>winCoords</code> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method differs from {@link #unproject(Vector3d, int[], Vector3d) unproject()} 
     * in that it assumes that <code>this</code> is already the inverse matrix of the original projection matrix.
     * It exists to avoid recomputing the matrix inverse with every invocation.
     * <p>
     * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
     * and then transforms those NDC coordinates by <code>this</code> matrix.  
     * <p>
     * The depth range of <tt>winCoords.z</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
     * 
     * @see #unproject(Vector3d, int[], Vector3d)
     * 
     * @param winCoords
     *          the window coordinates to unproject
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          will hold the unprojected position
     * @return dest
     */
    public Vector3d unprojectInv(Vector3d winCoords, int[] viewport, Vector3d dest) {
        return unprojectInv(winCoords.x, winCoords.y, winCoords.z, viewport, dest);
    }

    /**
     * Unproject the given window coordinates <tt>(winX, winY, winZ)</tt> by <code>this</code> matrix using the specified viewport.
     * <p>
     * This method differs from {@link #unproject(double, double, double, int[], Vector3d) unproject()} 
     * in that it assumes that <code>this</code> is already the inverse matrix of the original projection matrix.
     * It exists to avoid recomputing the matrix inverse with every invocation.
     * <p>
     * This method first converts the given window coordinates to normalized device coordinates in the range <tt>[-1..1]</tt>
     * and then transforms those NDC coordinates by <code>this</code> matrix.  
     * <p>
     * The depth range of <tt>winZ</tt> is assumed to be <tt>[0..1]</tt>, which is also the OpenGL default.
     * 
     * @see #unproject(double, double, double, int[], Vector3d)
     * 
     * @param winX
     *          the x-coordinate in window coordinates (pixels)
     * @param winY
     *          the y-coordinate in window coordinates (pixels)
     * @param winZ
     *          the z-coordinate, which is the depth value in <tt>[0..1]</tt>
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          will hold the unprojected position
     * @return dest
     */
    public Vector3d unprojectInv(double winX, double winY, double winZ, int[] viewport, Vector3d dest) {
        double ndcX = (winX-viewport[0])/viewport[2]*2.0-1.0;
        double ndcY = (winY-viewport[1])/viewport[3]*2.0-1.0;
        double ndcZ = winZ+winZ-1.0;
        dest.x = ms[M00] * ndcX + ms[M10] * ndcY + ms[M20] * ndcZ + ms[M30];
        dest.y = ms[M01] * ndcX + ms[M11] * ndcY + ms[M21] * ndcZ + ms[M31];
        dest.z = ms[M02] * ndcX + ms[M12] * ndcY + ms[M22] * ndcZ + ms[M32];
        double w = ms[M03] * ndcX + ms[M13] * ndcY + ms[M23] * ndcZ + ms[M33];
        dest.div(w);
        return dest;
    }

    /**
     * Project the given <tt>(x, y, z)</tt> position via <code>this</code> matrix using the specified viewport
     * and store the resulting window coordinates in <code>winCoordsDest</code>.
     * <p>
     * This method transforms the given coordinates by <code>this</code> matrix including perspective division to 
     * obtain normalized device coordinates, and then translates these into window coordinates by using the
     * given <code>viewport</code> settings <tt>[x, y, width, height]</tt>.
     * <p>
     * The depth range of the returned <code>winCoordsDest.z</code> will be <tt>[0..1]</tt>, which is also the OpenGL default.  
     * 
     * @param x
     *          the x-coordinate of the position to project
     * @param y
     *          the y-coordinate of the position to project
     * @param z
     *          the z-coordinate of the position to project
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param winCoordsDest
     *          will hold the projected window coordinates
     * @return winCoordsDest
     */
    public Vector4d project(double x, double y, double z, int[] viewport, Vector4d winCoordsDest) {
        winCoordsDest.x = ms[M00] * x + ms[M10] * y + ms[M20] * z + ms[M30];
        winCoordsDest.y = ms[M01] * x + ms[M11] * y + ms[M21] * z + ms[M31];
        winCoordsDest.z = ms[M02] * x + ms[M12] * y + ms[M22] * z + ms[M32];
        winCoordsDest.w = ms[M03] * x + ms[M13] * y + ms[M23] * z + ms[M33];
        winCoordsDest.div(winCoordsDest.w);
        winCoordsDest.x = (winCoordsDest.x*0.5+0.5) * viewport[2] + viewport[0];
        winCoordsDest.y = (winCoordsDest.y*0.5+0.5) * viewport[3] + viewport[1];
        winCoordsDest.z = (1.0+winCoordsDest.z)*0.5;
        return winCoordsDest;
    }

    /**
     * Project the given <tt>(x, y, z)</tt> position via <code>this</code> matrix using the specified viewport
     * and store the resulting window coordinates in <code>winCoordsDest</code>.
     * <p>
     * This method transforms the given coordinates by <code>this</code> matrix including perspective division to 
     * obtain normalized device coordinates, and then translates these into window coordinates by using the
     * given <code>viewport</code> settings <tt>[x, y, width, height]</tt>.
     * <p>
     * The depth range of the returned <code>winCoordsDest.z</code> will be <tt>[0..1]</tt>, which is also the OpenGL default.  
     * 
     * @param x
     *          the x-coordinate of the position to project
     * @param y
     *          the y-coordinate of the position to project
     * @param z
     *          the z-coordinate of the position to project
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param winCoordsDest
     *          will hold the projected window coordinates
     * @return winCoordsDest
     */
    public Vector3d project(double x, double y, double z, int[] viewport, Vector3d winCoordsDest) {
        winCoordsDest.x = ms[M00] * x + ms[M10] * y + ms[M20] * z + ms[M30];
        winCoordsDest.y = ms[M01] * x + ms[M11] * y + ms[M21] * z + ms[M31];
        winCoordsDest.z = ms[M02] * x + ms[M12] * y + ms[M22] * z + ms[M32];
        double w = ms[M03] * x + ms[M13] * y + ms[M23] * z + ms[M33];
        winCoordsDest.div(w);
        winCoordsDest.x = (winCoordsDest.x*0.5+0.5) * viewport[2] + viewport[0];
        winCoordsDest.y = (winCoordsDest.y*0.5+0.5) * viewport[3] + viewport[1];
        winCoordsDest.z = (1.0+winCoordsDest.z)*0.5;
        return winCoordsDest;
    }

    /**
     * Project the given <code>position</code> via <code>this</code> matrix using the specified viewport
     * and store the resulting window coordinates in <code>winCoordsDest</code>.
     * <p>
     * This method transforms the given coordinates by <code>this</code> matrix including perspective division to 
     * obtain normalized device coordinates, and then translates these into window coordinates by using the
     * given <code>viewport</code> settings <tt>[x, y, width, height]</tt>.
     * <p>
     * The depth range of the returned <code>winCoordsDest.z</code> will be <tt>[0..1]</tt>, which is also the OpenGL default.  
     * 
     * @see #project(double, double, double, int[], Vector4d)
     * 
     * @param position
     *          the position to project into window coordinates
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param winCoordsDest
     *          will hold the projected window coordinates
     * @return winCoordsDest
     */
    public Vector4d project(Vector3d position, int[] viewport, Vector4d winCoordsDest) {
        return project(position.x, position.y, position.z, viewport, winCoordsDest);
    }

    /**
     * Project the given <code>position</code> via <code>this</code> matrix using the specified viewport
     * and store the resulting window coordinates in <code>winCoordsDest</code>.
     * <p>
     * This method transforms the given coordinates by <code>this</code> matrix including perspective division to 
     * obtain normalized device coordinates, and then translates these into window coordinates by using the
     * given <code>viewport</code> settings <tt>[x, y, width, height]</tt>.
     * <p>
     * The depth range of the returned <code>winCoordsDest.z</code> will be <tt>[0..1]</tt>, which is also the OpenGL default.  
     * 
     * @see #project(double, double, double, int[], Vector4d)
     * 
     * @param position
     *          the position to project into window coordinates
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param winCoordsDest
     *          will hold the projected window coordinates
     * @return winCoordsDest
     */
    public Vector3d project(Vector3d position, int[] viewport, Vector3d winCoordsDest) {
        return project(position.x, position.y, position.z, viewport, winCoordsDest);
    }

    /**
     * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
     * specified via the equation <tt>x*a + y*b + z*c + d = 0</tt> and store the result in <code>dest</code>.
     * <p>
     * The vector <tt>(a, b, c)</tt> must be a unit vector.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the reflection matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * reflection will be applied first!
     * <p>
     * Reference: <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/bb281733(v=vs.85).aspx">msdn.microsoft.com</a>
     * 
     * @param a
     *          the x factor in the plane equation
     * @param b
     *          the y factor in the plane equation
     * @param c
     *          the z factor in the plane equation
     * @param d
     *          the constant in the plane equation
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d reflect(double a, double b, double c, double d, Matrix4d dest) {
        double da = a + a, db = b + b, dc = c + c, dd = d + d;
        double rn00 = 1.0 - da * a;
        double rn01 = -da * b;
        double rn02 = -da * c;
        double rn10 = -db * a;
        double rn11 = 1.0 - db * b;
        double rn12 = -db * c;
        double rn20 = -dc * a;
        double rn21 = -dc * b;
        double rn22 = 1.0 - dc * c;
        double rn30 = -dd * a;
        double rn31 = -dd * b;
        double rn32 = -dd * c;

        // matrix multiplication
        dest.ms[M30] = ms[M00] * rn30 + ms[M10] * rn31 + ms[M20] * rn32 + ms[M30];
        dest.ms[M31] = ms[M01] * rn30 + ms[M11] * rn31 + ms[M21] * rn32 + ms[M31];
        dest.ms[M32] = ms[M02] * rn30 + ms[M12] * rn31 + ms[M22] * rn32 + ms[M32];
        dest.ms[M33] = ms[M03] * rn30 + ms[M13] * rn31 + ms[M23] * rn32 + ms[M33];
        double nms00 = ms[M00] * rn00 + ms[M10] * rn01 + ms[M20] * rn02;
        double nms01 = ms[M01] * rn00 + ms[M11] * rn01 + ms[M21] * rn02;
        double nms02 = ms[M02] * rn00 + ms[M12] * rn01 + ms[M22] * rn02;
        double nms03 = ms[M03] * rn00 + ms[M13] * rn01 + ms[M23] * rn02;
        double nms10 = ms[M00] * rn10 + ms[M10] * rn11 + ms[M20] * rn12;
        double nms11 = ms[M01] * rn10 + ms[M11] * rn11 + ms[M21] * rn12;
        double nms12 = ms[M02] * rn10 + ms[M12] * rn11 + ms[M22] * rn12;
        double nms13 = ms[M03] * rn10 + ms[M13] * rn11 + ms[M23] * rn12;
        dest.ms[M20] = ms[M00] * rn20 + ms[M10] * rn21 + ms[M20] * rn22;
        dest.ms[M21] = ms[M01] * rn20 + ms[M11] * rn21 + ms[M21] * rn22;
        dest.ms[M22] = ms[M02] * rn20 + ms[M12] * rn21 + ms[M22] * rn22;
        dest.ms[M23] = ms[M03] * rn20 + ms[M13] * rn21 + ms[M23] * rn22;
        dest.ms[M00] = nms00;
        dest.ms[M01] = nms01;
        dest.ms[M02] = nms02;
        dest.ms[M03] = nms03;
        dest.ms[M10] = nms10;
        dest.ms[M11] = nms11;
        dest.ms[M12] = nms12;
        dest.ms[M13] = nms13;

        return dest;
    }

    /**
     * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
     * specified via the equation <tt>x*a + y*b + z*c + d = 0</tt>.
     * <p>
     * The vector <tt>(a, b, c)</tt> must be a unit vector.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the reflection matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * reflection will be applied first!
     * <p>
     * Reference: <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/bb281733(v=vs.85).aspx">msdn.microsoft.com</a>
     * 
     * @param a
     *          the x factor in the plane equation
     * @param b
     *          the y factor in the plane equation
     * @param c
     *          the z factor in the plane equation
     * @param d
     *          the constant in the plane equation
     * @return this
     */
    public Matrix4d reflect(double a, double b, double c, double d) {
        return reflect(a, b, c, d, this);
    }

    /**
     * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
     * specified via the plane normal and a point on the plane.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the reflection matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * reflection will be applied first!
     * 
     * @param nx
     *          the x-coordinate of the plane normal
     * @param ny
     *          the y-coordinate of the plane normal
     * @param nz
     *          the z-coordinate of the plane normal
     * @param px
     *          the x-coordinate of a point on the plane
     * @param py
     *          the y-coordinate of a point on the plane
     * @param pz
     *          the z-coordinate of a point on the plane
     * @return this
     */
    public Matrix4d reflect(double nx, double ny, double nz, double px, double py, double pz) {
        return reflect(nx, ny, nz, px, py, pz, this);
    }

    /**
     * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
     * specified via the plane normal and a point on the plane, and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the reflection matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * reflection will be applied first!
     * 
     * @param nx
     *          the x-coordinate of the plane normal
     * @param ny
     *          the y-coordinate of the plane normal
     * @param nz
     *          the z-coordinate of the plane normal
     * @param px
     *          the x-coordinate of a point on the plane
     * @param py
     *          the y-coordinate of a point on the plane
     * @param pz
     *          the z-coordinate of a point on the plane
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d reflect(double nx, double ny, double nz, double px, double py, double pz, Matrix4d dest) {
        double invLength = 1.0 / Math.sqrt(nx * nx + ny * ny + nz * nz);
        double nnx = nx * invLength;
        double nny = ny * invLength;
        double nnz = nz * invLength;
        /* See: http://mathworld.wolfram.com/Plane.html */
        return reflect(nnx, nny, nnz, -nnx * px - nny * py - nnz * pz, dest);
    }

    /**
     * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
     * specified via the plane normal and a point on the plane.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the reflection matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * reflection will be applied first!
     * 
     * @param normal
     *          the plane normal
     * @param point
     *          a point on the plane
     * @return this
     */
    public Matrix4d reflect(Vector3d normal, Vector3d point) {
        return reflect(normal.x, normal.y, normal.z, point.x, point.y, point.z);
    }

    /**
     * Apply a mirror/reflection transformation to this matrix that reflects about a plane
     * specified via the plane orientation and a point on the plane.
     * <p>
     * This method can be used to build a reflection transformation based on the orientation of a mirror object in the scene.
     * It is assumed that the default mirror plane's normal is <tt>(0, 0, 1)</tt>. So, if the given {@link Quaterniond} is
     * the identity (does not apply any additional rotation), the reflection plane will be <tt>z=0</tt>, offset by the given <code>point</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the reflection matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * reflection will be applied first!
     * 
     * @param orientation
     *          the plane orientation relative to an implied normal vector of <tt>(0, 0, 1)</tt>
     * @param point
     *          a point on the plane
     * @return this
     */
    public Matrix4d reflect(Quaterniond orientation, Vector3d point) {
        return reflect(orientation, point, this);
    }

    /**
     * Apply a mirror/reflection transformation to this matrix that reflects about a plane
     * specified via the plane orientation and a point on the plane, and store the result in <code>dest</code>.
     * <p>
     * This method can be used to build a reflection transformation based on the orientation of a mirror object in the scene.
     * It is assumed that the default mirror plane's normal is <tt>(0, 0, 1)</tt>. So, if the given {@link Quaterniond} is
     * the identity (does not apply any additional rotation), the reflection plane will be <tt>z=0</tt>, offset by the given <code>point</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the reflection matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * reflection will be applied first!
     * 
     * @param orientation
     *          the plane orientation
     * @param point
     *          a point on the plane
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d reflect(Quaterniond orientation, Vector3d point, Matrix4d dest) {
        double num1 = orientation.x + orientation.x;
        double num2 = orientation.y + orientation.y;
        double num3 = orientation.z + orientation.z;
        double normalX = orientation.x * num3 + orientation.w * num2;
        double normalY = orientation.y * num3 - orientation.w * num1;
        double normalZ = 1.0 - (orientation.x * num1 + orientation.y * num2);
        return reflect(normalX, normalY, normalZ, point.x, point.y, point.z, dest);
    }

    /**
     * Apply a mirror/reflection transformation to this matrix that reflects about the given plane
     * specified via the plane normal and a point on the plane, and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the reflection matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * reflection will be applied first!
     * 
     * @param normal
     *          the plane normal
     * @param point
     *          a point on the plane
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d reflect(Vector3d normal, Vector3d point, Matrix4d dest) {
        return reflect(normal.x, normal.y, normal.z, point.x, point.y, point.z, dest);
    }

    /**
     * Set this matrix to a mirror/reflection transformation that reflects about the given plane
     * specified via the equation <tt>x*a + y*b + z*c + d = 0</tt>.
     * <p>
     * The vector <tt>(a, b, c)</tt> must be a unit vector.
     * <p>
     * Reference: <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/bb281733(v=vs.85).aspx">msdn.microsoft.com</a>
     * 
     * @param a
     *          the x factor in the plane equation
     * @param b
     *          the y factor in the plane equation
     * @param c
     *          the z factor in the plane equation
     * @param d
     *          the constant in the plane equation
     * @return this
     */
    public Matrix4d reflection(double a, double b, double c, double d) {
        double da = a + a, db = b + b, dc = c + c, dd = d + d;
        ms[M00] = 1.0 - da * a;
        ms[M01] = -da * b;
        ms[M02] = -da * c;
        ms[M03] = 0.0;
        ms[M10] = -db * a;
        ms[M11] = 1.0 - db * b;
        ms[M12] = -db * c;
        ms[M13] = 0.0;
        ms[M20] = -dc * a;
        ms[M21] = -dc * b;
        ms[M22] = 1.0 - dc * c;
        ms[M23] = 0.0;
        ms[M30] = -dd * a;
        ms[M31] = -dd * b;
        ms[M32] = -dd * c;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to a mirror/reflection transformation that reflects about the given plane
     * specified via the plane normal and a point on the plane.
     * 
     * @param nx
     *          the x-coordinate of the plane normal
     * @param ny
     *          the y-coordinate of the plane normal
     * @param nz
     *          the z-coordinate of the plane normal
     * @param px
     *          the x-coordinate of a point on the plane
     * @param py
     *          the y-coordinate of a point on the plane
     * @param pz
     *          the z-coordinate of a point on the plane
     * @return this
     */
    public Matrix4d reflection(double nx, double ny, double nz, double px, double py, double pz) {
        double invLength = 1.0 / Math.sqrt(nx * nx + ny * ny + nz * nz);
        double nnx = nx * invLength;
        double nny = ny * invLength;
        double nnz = nz * invLength;
        /* See: http://mathworld.wolfram.com/Plane.html */
        return reflection(nnx, nny, nnz, -nnx * px - nny * py - nnz * pz);
    }

    /**
     * Set this matrix to a mirror/reflection transformation that reflects about the given plane
     * specified via the plane normal and a point on the plane.
     * 
     * @param normal
     *          the plane normal
     * @param point
     *          a point on the plane
     * @return this
     */
    public Matrix4d reflection(Vector3d normal, Vector3d point) {
        return reflection(normal.x, normal.y, normal.z, point.x, point.y, point.z);
    }

    /**
     * Set this matrix to a mirror/reflection transformation that reflects about a plane
     * specified via the plane orientation and a point on the plane.
     * <p>
     * This method can be used to build a reflection transformation based on the orientation of a mirror object in the scene.
     * It is assumed that the default mirror plane's normal is <tt>(0, 0, 1)</tt>. So, if the given {@link Quaterniond} is
     * the identity (does not apply any additional rotation), the reflection plane will be <tt>z=0</tt>, offset by the given <code>point</code>.
     * 
     * @param orientation
     *          the plane orientation
     * @param point
     *          a point on the plane
     * @return this
     */
    public Matrix4d reflection(Quaterniond orientation, Vector3d point) {
        double num1 = orientation.x + orientation.x;
        double num2 = orientation.y + orientation.y;
        double num3 = orientation.z + orientation.z;
        double normalX = orientation.x * num3 + orientation.w * num2;
        double normalY = orientation.y * num3 - orientation.w * num1;
        double normalZ = 1.0 - (orientation.x * num1 + orientation.y * num2);
        return reflection(normalX, normalY, normalZ, point.x, point.y, point.z);
    }

    /**
     * Apply an orthographic projection transformation using the given NDC z range to this matrix and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * <p>
     * In order to set the matrix to an orthographic projection without post-multiplying it,
     * use {@link #setOrtho(double, double, double, double, double, double, boolean) setOrtho()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #setOrtho(double, double, double, double, double, double, boolean)
     * 
     * @param left
     *            the distance from the center to the left frustum edge
     * @param right
     *            the distance from the center to the right frustum edge
     * @param bottom
     *            the distance from the center to the bottom frustum edge
     * @param top
     *            the distance from the center to the top frustum edge
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d ortho(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne, Matrix4d dest) {
        // calculate right matrix elements
        double rn00 = 2.0 / (right - left);
        double rn11 = 2.0 / (top - bottom);
        double rn22 = (zZeroToOne ? 1.0 : 2.0) / (zNear - zFar);
        double rn30 = (left + right) / (left - right);
        double rn31 = (top + bottom) / (bottom - top);
        double rn32 = (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar);

        // perform optimized multiplication
        // compute the last column first, because other columns do not depend on it
        dest.ms[M30] = ms[M00] * rn30 + ms[M10] * rn31 + ms[M20] * rn32 + ms[M30];
        dest.ms[M31] = ms[M01] * rn30 + ms[M11] * rn31 + ms[M21] * rn32 + ms[M31];
        dest.ms[M32] = ms[M02] * rn30 + ms[M12] * rn31 + ms[M22] * rn32 + ms[M32];
        dest.ms[M33] = ms[M03] * rn30 + ms[M13] * rn31 + ms[M23] * rn32 + ms[M33];
        dest.ms[M00] = ms[M00] * rn00;
        dest.ms[M01] = ms[M01] * rn00;
        dest.ms[M02] = ms[M02] * rn00;
        dest.ms[M03] = ms[M03] * rn00;
        dest.ms[M10] = ms[M10] * rn11;
        dest.ms[M11] = ms[M11] * rn11;
        dest.ms[M12] = ms[M12] * rn11;
        dest.ms[M13] = ms[M13] * rn11;
        dest.ms[M20] = ms[M20] * rn22;
        dest.ms[M21] = ms[M21] * rn22;
        dest.ms[M22] = ms[M22] * rn22;
        dest.ms[M23] = ms[M23] * rn22;

        return dest;
    }

    /**
     * Apply an orthographic projection transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * <p>
     * In order to set the matrix to an orthographic projection without post-multiplying it,
     * use {@link #setOrtho(double, double, double, double, double, double) setOrtho()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #setOrtho(double, double, double, double, double, double)
     * 
     * @param left
     *            the distance from the center to the left frustum edge
     * @param right
     *            the distance from the center to the right frustum edge
     * @param bottom
     *            the distance from the center to the bottom frustum edge
     * @param top
     *            the distance from the center to the top frustum edge
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d ortho(double left, double right, double bottom, double top, double zNear, double zFar, Matrix4d dest) {
        return ortho(left, right, bottom, top, zNear, zFar, false, dest);
    }

    /**
     * Apply an orthographic projection transformation using the given NDC z range to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * <p>
     * In order to set the matrix to an orthographic projection without post-multiplying it,
     * use {@link #setOrtho(double, double, double, double, double, double, boolean) setOrtho()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #setOrtho(double, double, double, double, double, double, boolean)
     * 
     * @param left
     *            the distance from the center to the left frustum edge
     * @param right
     *            the distance from the center to the right frustum edge
     * @param bottom
     *            the distance from the center to the bottom frustum edge
     * @param top
     *            the distance from the center to the top frustum edge
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @return this
     */
    public Matrix4d ortho(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne) {
        return ortho(left, right, bottom, top, zNear, zFar, zZeroToOne, this);
    }

    /**
     * Apply an orthographic projection transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * <p>
     * In order to set the matrix to an orthographic projection without post-multiplying it,
     * use {@link #setOrtho(double, double, double, double, double, double) setOrtho()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #setOrtho(double, double, double, double, double, double)
     * 
     * @param left
     *            the distance from the center to the left frustum edge
     * @param right
     *            the distance from the center to the right frustum edge
     * @param bottom
     *            the distance from the center to the bottom frustum edge
     * @param top
     *            the distance from the center to the top frustum edge
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @return this
     */
    public Matrix4d ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
        return ortho(left, right, bottom, top, zNear, zFar, false);
    }

    /**
     * Set this matrix to be an orthographic projection transformation using the given NDC z range.
     * <p>
     * In order to apply the orthographic projection to an already existing transformation,
     * use {@link #ortho(double, double, double, double, double, double, boolean) ortho()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #ortho(double, double, double, double, double, double, boolean)
     * 
     * @param left
     *            the distance from the center to the left frustum edge
     * @param right
     *            the distance from the center to the right frustum edge
     * @param bottom
     *            the distance from the center to the bottom frustum edge
     * @param top
     *            the distance from the center to the top frustum edge
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @return this
     */
    public Matrix4d setOrtho(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne) {
        ms[M00] = 2.0 / (right - left);
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = 2.0 / (top - bottom);
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        ms[M22] = (zZeroToOne ? 1.0 : 2.0) / (zNear - zFar);
        ms[M23] = 0.0;
        ms[M30] = (right + left) / (left - right);
        ms[M31] = (top + bottom) / (bottom - top);
        ms[M32] = (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar);
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to be an orthographic projection transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
     * <p>
     * In order to apply the orthographic projection to an already existing transformation,
     * use {@link #ortho(double, double, double, double, double, double) ortho()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #ortho(double, double, double, double, double, double)
     * 
     * @param left
     *            the distance from the center to the left frustum edge
     * @param right
     *            the distance from the center to the right frustum edge
     * @param bottom
     *            the distance from the center to the bottom frustum edge
     * @param top
     *            the distance from the center to the top frustum edge
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @return this
     */
    public Matrix4d setOrtho(double left, double right, double bottom, double top, double zNear, double zFar) {
        return setOrtho(left, right, bottom, top, zNear, zFar, false);
    }

    /**
     * Apply a symmetric orthographic projection transformation using the given NDC z range to this matrix and store the result in <code>dest</code>.
     * <p>
     * This method is equivalent to calling {@link #ortho(double, double, double, double, double, double, boolean, Matrix4d) ortho()} with
     * <code>left=-width/2</code>, <code>right=+width/2</code>, <code>bottom=-height/2</code> and <code>top=+height/2</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * <p>
     * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
     * use {@link #setOrthoSymmetric(double, double, double, double, boolean) setOrthoSymmetric()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #setOrthoSymmetric(double, double, double, double, boolean)
     * 
     * @param width
     *            the distance between the right and left frustum edges
     * @param height
     *            the distance between the top and bottom frustum edges
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @param dest
     *            will hold the result
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @return dest
     */
    public Matrix4d orthoSymmetric(double width, double height, double zNear, double zFar, boolean zZeroToOne, Matrix4d dest) {
        // calculate right matrix elements
        double rn00 = 2.0 / width;
        double rn11 = 2.0 / height;
        double rn22 = (zZeroToOne ? 1.0 : 2.0) / (zNear - zFar);
        double rn32 = (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar);

        // perform optimized multiplication
        // compute the last column first, because other columns do not depend on it
        dest.ms[M30] = ms[M20] * rn32 + ms[M30];
        dest.ms[M31] = ms[M21] * rn32 + ms[M31];
        dest.ms[M32] = ms[M22] * rn32 + ms[M32];
        dest.ms[M33] = ms[M23] * rn32 + ms[M33];
        dest.ms[M00] = ms[M00] * rn00;
        dest.ms[M01] = ms[M01] * rn00;
        dest.ms[M02] = ms[M02] * rn00;
        dest.ms[M03] = ms[M03] * rn00;
        dest.ms[M10] = ms[M10] * rn11;
        dest.ms[M11] = ms[M11] * rn11;
        dest.ms[M12] = ms[M12] * rn11;
        dest.ms[M13] = ms[M13] * rn11;
        dest.ms[M20] = ms[M20] * rn22;
        dest.ms[M21] = ms[M21] * rn22;
        dest.ms[M22] = ms[M22] * rn22;
        dest.ms[M23] = ms[M23] * rn22;

        return dest;
    }

    /**
     * Apply a symmetric orthographic projection transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in <code>dest</code>.
     * <p>
     * This method is equivalent to calling {@link #ortho(double, double, double, double, double, double, Matrix4d) ortho()} with
     * <code>left=-width/2</code>, <code>right=+width/2</code>, <code>bottom=-height/2</code> and <code>top=+height/2</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * <p>
     * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
     * use {@link #setOrthoSymmetric(double, double, double, double) setOrthoSymmetric()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #setOrthoSymmetric(double, double, double, double)
     * 
     * @param width
     *            the distance between the right and left frustum edges
     * @param height
     *            the distance between the top and bottom frustum edges
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d orthoSymmetric(double width, double height, double zNear, double zFar, Matrix4d dest) {
        return orthoSymmetric(width, height, zNear, zFar, false, dest);
    }

    /**
     * Apply a symmetric orthographic projection transformation using the given NDC z range to this matrix.
     * <p>
     * This method is equivalent to calling {@link #ortho(double, double, double, double, double, double, boolean) ortho()} with
     * <code>left=-width/2</code>, <code>right=+width/2</code>, <code>bottom=-height/2</code> and <code>top=+height/2</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * <p>
     * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
     * use {@link #setOrthoSymmetric(double, double, double, double, boolean) setOrthoSymmetric()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #setOrthoSymmetric(double, double, double, double, boolean)
     * 
     * @param width
     *            the distance between the right and left frustum edges
     * @param height
     *            the distance between the top and bottom frustum edges
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @return this
     */
    public Matrix4d orthoSymmetric(double width, double height, double zNear, double zFar, boolean zZeroToOne) {
        return orthoSymmetric(width, height, zNear, zFar, zZeroToOne, this);
    }

    /**
     * Apply a symmetric orthographic projection transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
     * <p>
     * This method is equivalent to calling {@link #ortho(double, double, double, double, double, double) ortho()} with
     * <code>left=-width/2</code>, <code>right=+width/2</code>, <code>bottom=-height/2</code> and <code>top=+height/2</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * <p>
     * In order to set the matrix to a symmetric orthographic projection without post-multiplying it,
     * use {@link #setOrthoSymmetric(double, double, double, double) setOrthoSymmetric()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #setOrthoSymmetric(double, double, double, double)
     * 
     * @param width
     *            the distance between the right and left frustum edges
     * @param height
     *            the distance between the top and bottom frustum edges
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @return this
     */
    public Matrix4d orthoSymmetric(double width, double height, double zNear, double zFar) {
        return orthoSymmetric(width, height, zNear, zFar, false, this);
    }

    /**
     * Set this matrix to be a symmetric orthographic projection transformation using the given NDC z range.
     * <p>
     * This method is equivalent to calling {@link #setOrtho(double, double, double, double, double, double, boolean) setOrtho()} with
     * <code>left=-width/2</code>, <code>right=+width/2</code>, <code>bottom=-height/2</code> and <code>top=+height/2</code>.
     * <p>
     * In order to apply the symmetric orthographic projection to an already existing transformation,
     * use {@link #orthoSymmetric(double, double, double, double, boolean) orthoSymmetric()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #orthoSymmetric(double, double, double, double, boolean)
     * 
     * @param width
     *            the distance between the right and left frustum edges
     * @param height
     *            the distance between the top and bottom frustum edges
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @return this
     */
    public Matrix4d setOrthoSymmetric(double width, double height, double zNear, double zFar, boolean zZeroToOne) {
        ms[M00] = 2.0 / width;
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = 2.0 / height;
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        ms[M22] = (zZeroToOne ? 1.0 : 2.0) / (zNear - zFar);
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar);
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to be a symmetric orthographic projection transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
     * <p>
     * This method is equivalent to calling {@link #setOrtho(double, double, double, double, double, double) setOrtho()} with
     * <code>left=-width/2</code>, <code>right=+width/2</code>, <code>bottom=-height/2</code> and <code>top=+height/2</code>.
     * <p>
     * In order to apply the symmetric orthographic projection to an already existing transformation,
     * use {@link #orthoSymmetric(double, double, double, double) orthoSymmetric()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #orthoSymmetric(double, double, double, double)
     * 
     * @param width
     *            the distance between the right and left frustum edges
     * @param height
     *            the distance between the top and bottom frustum edges
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     * @return this
     */
    public Matrix4d setOrthoSymmetric(double width, double height, double zNear, double zFar) {
        return setOrthoSymmetric(width, height, zNear, zFar, false);
    }

    /**
     * Apply an orthographic projection transformation to this matrix and store the result in <code>dest</code>.
     * <p>
     * This method is equivalent to calling {@link #ortho(double, double, double, double, double, double, Matrix4d) ortho()} with
     * <code>zNear=-1</code> and <code>zFar=+1</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * <p>
     * In order to set the matrix to an orthographic projection without post-multiplying it,
     * use {@link #setOrtho2D(double, double, double, double) setOrtho()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #ortho(double, double, double, double, double, double, Matrix4d)
     * @see #setOrtho2D(double, double, double, double)
     * 
     * @param left
     *            the distance from the center to the left frustum edge
     * @param right
     *            the distance from the center to the right frustum edge
     * @param bottom
     *            the distance from the center to the bottom frustum edge
     * @param top
     *            the distance from the center to the top frustum edge
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d ortho2D(double left, double right, double bottom, double top, Matrix4d dest) {
        // calculate right matrix elements
        double rn00 = 2.0 / (right - left);
        double rn11 = 2.0 / (top - bottom);
        double rn30 = -(right + left) / (right - left);
        double rn31 = -(top + bottom) / (top - bottom);

        // perform optimized multiplication
        // compute the last column first, because other columns do not depend on it
        dest.ms[M30] = ms[M00] * rn30 + ms[M10] * rn31 + ms[M30];
        dest.ms[M31] = ms[M01] * rn30 + ms[M11] * rn31 + ms[M31];
        dest.ms[M32] = ms[M02] * rn30 + ms[M12] * rn31 + ms[M32];
        dest.ms[M33] = ms[M03] * rn30 + ms[M13] * rn31 + ms[M33];
        dest.ms[M00] = ms[M00] * rn00;
        dest.ms[M01] = ms[M01] * rn00;
        dest.ms[M02] = ms[M02] * rn00;
        dest.ms[M03] = ms[M03] * rn00;
        dest.ms[M10] = ms[M10] * rn11;
        dest.ms[M11] = ms[M11] * rn11;
        dest.ms[M12] = ms[M12] * rn11;
        dest.ms[M13] = ms[M13] * rn11;
        dest.ms[M20] = -ms[M20];
        dest.ms[M21] = -ms[M21];
        dest.ms[M22] = -ms[M22];
        dest.ms[M23] = -ms[M23];

        return dest;
    }

    /**
     * Apply an orthographic projection transformation to this matrix.
     * <p>
     * This method is equivalent to calling {@link #ortho(double, double, double, double, double, double) ortho()} with
     * <code>zNear=-1</code> and <code>zFar=+1</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>O</code> the orthographic projection matrix,
     * then the new matrix will be <code>M * O</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * O * v</code>, the
     * orthographic projection transformation will be applied first!
     * <p>
     * In order to set the matrix to an orthographic projection without post-multiplying it,
     * use {@link #setOrtho2D(double, double, double, double) setOrtho2D()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #ortho(double, double, double, double, double, double)
     * @see #setOrtho2D(double, double, double, double)
     * 
     * @param left
     *            the distance from the center to the left frustum edge
     * @param right
     *            the distance from the center to the right frustum edge
     * @param bottom
     *            the distance from the center to the bottom frustum edge
     * @param top
     *            the distance from the center to the top frustum edge
     * @return this
     */
    public Matrix4d ortho2D(double left, double right, double bottom, double top) {
        return ortho2D(left, right, bottom, top, this);
    }

    /**
     * Set this matrix to be an orthographic projection transformation.
     * <p>
     * This method is equivalent to calling {@link #setOrtho(double, double, double, double, double, double) setOrtho()} with
     * <code>zNear=-1</code> and <code>zFar=+1</code>.
     * <p>
     * In order to apply the orthographic projection to an already existing transformation,
     * use {@link #ortho2D(double, double, double, double) ortho2D()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#ortho">http://www.songho.ca</a>
     * 
     * @see #setOrtho(double, double, double, double, double, double)
     * @see #ortho2D(double, double, double, double)
     * 
     * @param left
     *            the distance from the center to the left frustum edge
     * @param right
     *            the distance from the center to the right frustum edge
     * @param bottom
     *            the distance from the center to the bottom frustum edge
     * @param top
     *            the distance from the center to the top frustum edge
     * @return this
     */
    public Matrix4d setOrtho2D(double left, double right, double bottom, double top) {
        ms[M00] = 2.0 / (right - left);
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = 2.0 / (top - bottom);
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        ms[M22] = -1.0;
        ms[M23] = 0.0;
        ms[M30] = -(right + left) / (right - left);
        ms[M31] = -(top + bottom) / (top - bottom);
        ms[M32] = 0.0;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Apply a rotation transformation to this matrix to make <code>-z</code> point along <code>dir</code>. 
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>L</code> the lookalong rotation matrix,
     * then the new matrix will be <code>M * L</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * L * v</code>, the
     * lookalong rotation transformation will be applied first!
     * <p>
     * This is equivalent to calling
     * {@link #lookAt(Vector3d, Vector3d, Vector3d) lookAt}
     * with <code>eye = (0, 0, 0)</code> and <code>center = dir</code>.
     * <p>
     * In order to set the matrix to a lookalong transformation without post-multiplying it,
     * use {@link #setLookAlong(Vector3d, Vector3d) setLookAlong()}.
     * 
     * @see #lookAlong(double, double, double, double, double, double)
     * @see #lookAt(Vector3d, Vector3d, Vector3d)
     * @see #setLookAlong(Vector3d, Vector3d)
     * 
     * @param dir
     *            the direction in space to look along
     * @param up
     *            the direction of 'up'
     * @return this
     */
    public Matrix4d lookAlong(Vector3d dir, Vector3d up) {
        return lookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z, this);
    }

    /**
     * Apply a rotation transformation to this matrix to make <code>-z</code> point along <code>dir</code>
     * and store the result in <code>dest</code>. 
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>L</code> the lookalong rotation matrix,
     * then the new matrix will be <code>M * L</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * L * v</code>, the
     * lookalong rotation transformation will be applied first!
     * <p>
     * This is equivalent to calling
     * {@link #lookAt(Vector3d, Vector3d, Vector3d) lookAt}
     * with <code>eye = (0, 0, 0)</code> and <code>center = dir</code>.
     * <p>
     * In order to set the matrix to a lookalong transformation without post-multiplying it,
     * use {@link #setLookAlong(Vector3d, Vector3d) setLookAlong()}.
     * 
     * @see #lookAlong(double, double, double, double, double, double)
     * @see #lookAt(Vector3d, Vector3d, Vector3d)
     * @see #setLookAlong(Vector3d, Vector3d)
     * 
     * @param dir
     *            the direction in space to look along
     * @param up
     *            the direction of 'up'
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d lookAlong(Vector3d dir, Vector3d up, Matrix4d dest) {
        return lookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z, dest);
    }

    /**
     * Apply a rotation transformation to this matrix to make <code>-z</code> point along <code>dir</code>
     * and store the result in <code>dest</code>. 
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>L</code> the lookalong rotation matrix,
     * then the new matrix will be <code>M * L</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * L * v</code>, the
     * lookalong rotation transformation will be applied first!
     * <p>
     * This is equivalent to calling
     * {@link #lookAt(double, double, double, double, double, double, double, double, double) lookAt()}
     * with <code>eye = (0, 0, 0)</code> and <code>center = dir</code>.
     * <p>
     * In order to set the matrix to a lookalong transformation without post-multiplying it,
     * use {@link #setLookAlong(double, double, double, double, double, double) setLookAlong()}
     * 
     * @see #lookAt(double, double, double, double, double, double, double, double, double)
     * @see #setLookAlong(double, double, double, double, double, double)
     * 
     * @param dirX
     *              the x-coordinate of the direction to look along
     * @param dirY
     *              the y-coordinate of the direction to look along
     * @param dirZ
     *              the z-coordinate of the direction to look along
     * @param upX
     *              the x-coordinate of the up vector
     * @param upY
     *              the y-coordinate of the up vector
     * @param upZ
     *              the z-coordinate of the up vector
     * @param dest
     *              will hold the result
     * @return dest
     */
    public Matrix4d lookAlong(double dirX, double dirY, double dirZ,
                              double upX, double upY, double upZ, Matrix4d dest) {
        // Normalize direction
        double invDirLength = 1.0 / Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        double dirnX = dirX * invDirLength;
        double dirnY = dirY * invDirLength;
        double dirnZ = dirZ * invDirLength;
        // right = direction x up
        double rightX, rightY, rightZ;
        rightX = dirnY * upZ - dirnZ * upY;
        rightY = dirnZ * upX - dirnX * upZ;
        rightZ = dirnX * upY - dirnY * upX;
        // normalize right
        double invRightLength = 1.0 / Math.sqrt(rightX * rightX + rightY * rightY + rightZ * rightZ);
        rightX *= invRightLength;
        rightY *= invRightLength;
        rightZ *= invRightLength;
        // up = right x direction
        double upnX = rightY * dirnZ - rightZ * dirnY;
        double upnY = rightZ * dirnX - rightX * dirnZ;
        double upnZ = rightX * dirnY - rightY * dirnX;

        // calculate right matrix elements
        double rn00 = rightX;
        double rn01 = upnX;
        double rn02 = -dirnX;
        double rn10 = rightY;
        double rn11 = upnY;
        double rn12 = -dirnY;
        double rn20 = rightZ;
        double rn21 = upnZ;
        double rn22 = -dirnZ;

        // perform optimized matrix multiplication
        // introduce temporaries for dependent results
        double nms00 = ms[M00] * rn00 + ms[M10] * rn01 + ms[M20] * rn02;
        double nms01 = ms[M01] * rn00 + ms[M11] * rn01 + ms[M21] * rn02;
        double nms02 = ms[M02] * rn00 + ms[M12] * rn01 + ms[M22] * rn02;
        double nms03 = ms[M03] * rn00 + ms[M13] * rn01 + ms[M23] * rn02;
        double nms10 = ms[M00] * rn10 + ms[M10] * rn11 + ms[M20] * rn12;
        double nms11 = ms[M01] * rn10 + ms[M11] * rn11 + ms[M21] * rn12;
        double nms12 = ms[M02] * rn10 + ms[M12] * rn11 + ms[M22] * rn12;
        double nms13 = ms[M03] * rn10 + ms[M13] * rn11 + ms[M23] * rn12;
        dest.ms[M20] = ms[M00] * rn20 + ms[M10] * rn21 + ms[M20] * rn22;
        dest.ms[M21] = ms[M01] * rn20 + ms[M11] * rn21 + ms[M21] * rn22;
        dest.ms[M22] = ms[M02] * rn20 + ms[M12] * rn21 + ms[M22] * rn22;
        dest.ms[M23] = ms[M03] * rn20 + ms[M13] * rn21 + ms[M23] * rn22;
        // set the rest of the matrix elements
        dest.ms[M00] = nms00;
        dest.ms[M01] = nms01;
        dest.ms[M02] = nms02;
        dest.ms[M03] = nms03;
        dest.ms[M10] = nms10;
        dest.ms[M11] = nms11;
        dest.ms[M12] = nms12;
        dest.ms[M13] = nms13;
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];

        return dest;
    }

    /**
     * Apply a rotation transformation to this matrix to make <code>-z</code> point along <code>dir</code>. 
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>L</code> the lookalong rotation matrix,
     * then the new matrix will be <code>M * L</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * L * v</code>, the
     * lookalong rotation transformation will be applied first!
     * <p>
     * This is equivalent to calling
     * {@link #lookAt(double, double, double, double, double, double, double, double, double) lookAt()}
     * with <code>eye = (0, 0, 0)</code> and <code>center = dir</code>.
     * <p>
     * In order to set the matrix to a lookalong transformation without post-multiplying it,
     * use {@link #setLookAlong(double, double, double, double, double, double) setLookAlong()}
     * 
     * @see #lookAt(double, double, double, double, double, double, double, double, double)
     * @see #setLookAlong(double, double, double, double, double, double)
     * 
     * @param dirX
     *              the x-coordinate of the direction to look along
     * @param dirY
     *              the y-coordinate of the direction to look along
     * @param dirZ
     *              the z-coordinate of the direction to look along
     * @param upX
     *              the x-coordinate of the up vector
     * @param upY
     *              the y-coordinate of the up vector
     * @param upZ
     *              the z-coordinate of the up vector
     * @return this
     */
    public Matrix4d lookAlong(double dirX, double dirY, double dirZ,
                              double upX, double upY, double upZ) {
        return lookAlong(dirX, dirY, dirZ, upX, upY, upZ, this);
    }

    /**
     * Set this matrix to a rotation transformation to make <code>-z</code>
     * point along <code>dir</code>.
     * <p>
     * This is equivalent to calling
     * {@link #setLookAt(Vector3d, Vector3d, Vector3d) setLookAt()} 
     * with <code>eye = (0, 0, 0)</code> and <code>center = dir</code>.
     * <p>
     * In order to apply the lookalong transformation to any previous existing transformation,
     * use {@link #lookAlong(Vector3d, Vector3d)}.
     * 
     * @see #setLookAlong(Vector3d, Vector3d)
     * @see #lookAlong(Vector3d, Vector3d)
     * 
     * @param dir
     *            the direction in space to look along
     * @param up
     *            the direction of 'up'
     * @return this
     */
    public Matrix4d setLookAlong(Vector3d dir, Vector3d up) {
        return setLookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z);
    }

    /**
     * Set this matrix to a rotation transformation to make <code>-z</code>
     * point along <code>dir</code>.
     * <p>
     * This is equivalent to calling
     * {@link #setLookAt(double, double, double, double, double, double, double, double, double)
     * setLookAt()} with <code>eye = (0, 0, 0)</code> and <code>center = dir</code>.
     * <p>
     * In order to apply the lookalong transformation to any previous existing transformation,
     * use {@link #lookAlong(double, double, double, double, double, double) lookAlong()}
     * 
     * @see #setLookAlong(double, double, double, double, double, double)
     * @see #lookAlong(double, double, double, double, double, double)
     * 
     * @param dirX
     *              the x-coordinate of the direction to look along
     * @param dirY
     *              the y-coordinate of the direction to look along
     * @param dirZ
     *              the z-coordinate of the direction to look along
     * @param upX
     *              the x-coordinate of the up vector
     * @param upY
     *              the y-coordinate of the up vector
     * @param upZ
     *              the z-coordinate of the up vector
     * @return this
     */
    public Matrix4d setLookAlong(double dirX, double dirY, double dirZ,
                                 double upX, double upY, double upZ) {
        // Normalize direction
        double invDirLength = 1.0 / Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        double dirnX = dirX * invDirLength;
        double dirnY = dirY * invDirLength;
        double dirnZ = dirZ * invDirLength;
        // right = direction x up
        double rightX, rightY, rightZ;
        rightX = dirnY * upZ - dirnZ * upY;
        rightY = dirnZ * upX - dirnX * upZ;
        rightZ = dirnX * upY - dirnY * upX;
        // normalize right
        double invRightLength = 1.0 / Math.sqrt(rightX * rightX + rightY * rightY + rightZ * rightZ);
        rightX *= invRightLength;
        rightY *= invRightLength;
        rightZ *= invRightLength;
        // up = right x direction
        double upnX = rightY * dirnZ - rightZ * dirnY;
        double upnY = rightZ * dirnX - rightX * dirnZ;
        double upnZ = rightX * dirnY - rightY * dirnX;

        ms[M00] = rightX;
        ms[M01] = upnX;
        ms[M02] = -dirnX;
        ms[M03] = 0.0;
        ms[M10] = rightY;
        ms[M11] = upnY;
        ms[M12] = -dirnY;
        ms[M13] = 0.0;
        ms[M20] = rightZ;
        ms[M21] = upnZ;
        ms[M22] = -dirnZ;
        ms[M23] = 0.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M32] = 0.0;
        ms[M33] = 1.0;

        return this;
    }

    /**
     * Set this matrix to be a "lookat" transformation for a right-handed coordinate system, that aligns
     * <code>-z</code> with <code>center - eye</code>.
     * <p>
     * In order to not make use of vectors to specify <code>eye</code>, <code>center</code> and <code>up</code> but use primitives,
     * like in the GLU function, use {@link #setLookAt(double, double, double, double, double, double, double, double, double) setLookAt()}
     * instead.
     * <p>
     * In order to apply the lookat transformation to a previous existing transformation,
     * use {@link #lookAt(Vector3d, Vector3d, Vector3d) lookAt()}.
     * 
     * @see #setLookAt(double, double, double, double, double, double, double, double, double)
     * @see #lookAt(Vector3d, Vector3d, Vector3d)
     * 
     * @param eye
     *            the position of the camera
     * @param center
     *            the point in space to look at
     * @param up
     *            the direction of 'up'
     * @return this
     */
    public Matrix4d setLookAt(Vector3d eye, Vector3d center, Vector3d up) {
        return setLookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z);
    }

    /**
     * Set this matrix to be a "lookat" transformation for a right-handed coordinate system, 
     * that aligns <code>-z</code> with <code>center - eye</code>.
     * <p>
     * In order to apply the lookat transformation to a previous existing transformation,
     * use {@link #lookAt(double, double, double, double, double, double, double, double, double) lookAt}.
     * 
     * @see #setLookAt(Vector3d, Vector3d, Vector3d)
     * @see #lookAt(double, double, double, double, double, double, double, double, double)
     * 
     * @param eyeX
     *              the x-coordinate of the eye/camera location
     * @param eyeY
     *              the y-coordinate of the eye/camera location
     * @param eyeZ
     *              the z-coordinate of the eye/camera location
     * @param centerX
     *              the x-coordinate of the point to look at
     * @param centerY
     *              the y-coordinate of the point to look at
     * @param centerZ
     *              the z-coordinate of the point to look at
     * @param upX
     *              the x-coordinate of the up vector
     * @param upY
     *              the y-coordinate of the up vector
     * @param upZ
     *              the z-coordinate of the up vector
     * @return this
     */
    public Matrix4d setLookAt(double eyeX, double eyeY, double eyeZ,
                              double centerX, double centerY, double centerZ,
                              double upX, double upY, double upZ) {
        // Compute direction from position to lookAt
        double dirX, dirY, dirZ;
        dirX = eyeX - centerX;
        dirY = eyeY - centerY;
        dirZ = eyeZ - centerZ;
        // Normalize direction
        double invDirLength = 1.0 / Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX *= invDirLength;
        dirY *= invDirLength;
        dirZ *= invDirLength;
        // left = up x direction
        double leftX, leftY, leftZ;
        leftX = upY * dirZ - upZ * dirY;
        leftY = upZ * dirX - upX * dirZ;
        leftZ = upX * dirY - upY * dirX;
        // normalize left
        double invLeftLength = 1.0 / Math.sqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        leftX *= invLeftLength;
        leftY *= invLeftLength;
        leftZ *= invLeftLength;
        // up = direction x left
        double upnX = dirY * leftZ - dirZ * leftY;
        double upnY = dirZ * leftX - dirX * leftZ;
        double upnZ = dirX * leftY - dirY * leftX;

        ms[M00] = leftX;
        ms[M01] = upnX;
        ms[M02] = dirX;
        ms[M03] = 0.0;
        ms[M10] = leftY;
        ms[M11] = upnY;
        ms[M12] = dirY;
        ms[M13] = 0.0;
        ms[M20] = leftZ;
        ms[M21] = upnZ;
        ms[M22] = dirZ;
        ms[M23] = 0.0;
        ms[M30] = -(leftX * eyeX + leftY * eyeY + leftZ * eyeZ);
        ms[M31] = -(upnX * eyeX + upnY * eyeY + upnZ * eyeZ);
        ms[M32] = -(dirX * eyeX + dirY * eyeY + dirZ * eyeZ);
        ms[M33] = 1.0;

        return this;
    }

    /**
     * Apply a "lookat" transformation to this matrix for a right-handed coordinate system, 
     * that aligns <code>-z</code> with <code>center - eye</code> and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>L</code> the lookat matrix,
     * then the new matrix will be <code>M * L</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * L * v</code>,
     * the lookat transformation will be applied first!
     * <p>
     * In order to set the matrix to a lookat transformation without post-multiplying it,
     * use {@link #setLookAt(Vector3d, Vector3d, Vector3d)}.
     * 
     * @see #lookAt(double, double, double, double, double, double, double, double, double)
     * @see #setLookAlong(Vector3d, Vector3d)
     * 
     * @param eye
     *            the position of the camera
     * @param center
     *            the point in space to look at
     * @param up
     *            the direction of 'up'
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d lookAt(Vector3d eye, Vector3d center, Vector3d up, Matrix4d dest) {
        return lookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z, dest);
    }

    /**
     * Apply a "lookat" transformation to this matrix for a right-handed coordinate system, 
     * that aligns <code>-z</code> with <code>center - eye</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>L</code> the lookat matrix,
     * then the new matrix will be <code>M * L</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * L * v</code>,
     * the lookat transformation will be applied first!
     * <p>
     * In order to set the matrix to a lookat transformation without post-multiplying it,
     * use {@link #setLookAt(Vector3d, Vector3d, Vector3d)}.
     * 
     * @see #lookAt(double, double, double, double, double, double, double, double, double)
     * @see #setLookAlong(Vector3d, Vector3d)
     * 
     * @param eye
     *            the position of the camera
     * @param center
     *            the point in space to look at
     * @param up
     *            the direction of 'up'
     * @return this
     */
    public Matrix4d lookAt(Vector3d eye, Vector3d center, Vector3d up) {
        return lookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z, this);
    }

    /**
     * Apply a "lookat" transformation to this matrix for a right-handed coordinate system, 
     * that aligns <code>-z</code> with <code>center - eye</code> and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>L</code> the lookat matrix,
     * then the new matrix will be <code>M * L</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * L * v</code>,
     * the lookat transformation will be applied first!
     * <p>
     * In order to set the matrix to a lookat transformation without post-multiplying it,
     * use {@link #setLookAt(double, double, double, double, double, double, double, double, double) setLookAt()}.
     * 
     * @see #lookAt(Vector3d, Vector3d, Vector3d)
     * @see #setLookAt(double, double, double, double, double, double, double, double, double)
     * 
     * @param eyeX
     *              the x-coordinate of the eye/camera location
     * @param eyeY
     *              the y-coordinate of the eye/camera location
     * @param eyeZ
     *              the z-coordinate of the eye/camera location
     * @param centerX
     *              the x-coordinate of the point to look at
     * @param centerY
     *              the y-coordinate of the point to look at
     * @param centerZ
     *              the z-coordinate of the point to look at
     * @param upX
     *              the x-coordinate of the up vector
     * @param upY
     *              the y-coordinate of the up vector
     * @param upZ
     *              the z-coordinate of the up vector
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d lookAt(double eyeX, double eyeY, double eyeZ,
                           double centerX, double centerY, double centerZ,
                           double upX, double upY, double upZ, Matrix4d dest) {
        // Compute direction from position to lookAt
        double dirX, dirY, dirZ;
        dirX = eyeX - centerX;
        dirY = eyeY - centerY;
        dirZ = eyeZ - centerZ;
        // Normalize direction
        double invDirLength = 1.0 / Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX *= invDirLength;
        dirY *= invDirLength;
        dirZ *= invDirLength;
        // left = up x direction
        double leftX, leftY, leftZ;
        leftX = upY * dirZ - upZ * dirY;
        leftY = upZ * dirX - upX * dirZ;
        leftZ = upX * dirY - upY * dirX;
        // normalize left
        double invLeftLength = 1.0 / Math.sqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        leftX *= invLeftLength;
        leftY *= invLeftLength;
        leftZ *= invLeftLength;
        // up = direction x left
        double upnX = dirY * leftZ - dirZ * leftY;
        double upnY = dirZ * leftX - dirX * leftZ;
        double upnZ = dirX * leftY - dirY * leftX;

        // calculate right matrix elements
        double rn00 = leftX;
        double rn01 = upnX;
        double rn02 = dirX;
        double rn10 = leftY;
        double rn11 = upnY;
        double rn12 = dirY;
        double rn20 = leftZ;
        double rn21 = upnZ;
        double rn22 = dirZ;
        double rn30 = -(leftX * eyeX + leftY * eyeY + leftZ * eyeZ);
        double rn31 = -(upnX * eyeX + upnY * eyeY + upnZ * eyeZ);
        double rn32 = -(dirX * eyeX + dirY * eyeY + dirZ * eyeZ);

        // perform optimized matrix multiplication
        // compute last column first, because others do not depend on it
        dest.ms[M30] = ms[M00] * rn30 + ms[M10] * rn31 + ms[M20] * rn32 + ms[M30];
        dest.ms[M31] = ms[M01] * rn30 + ms[M11] * rn31 + ms[M21] * rn32 + ms[M31];
        dest.ms[M32] = ms[M02] * rn30 + ms[M12] * rn31 + ms[M22] * rn32 + ms[M32];
        dest.ms[M33] = ms[M03] * rn30 + ms[M13] * rn31 + ms[M23] * rn32 + ms[M33];
        // introduce temporaries for dependent results
        double nms00 = ms[M00] * rn00 + ms[M10] * rn01 + ms[M20] * rn02;
        double nms01 = ms[M01] * rn00 + ms[M11] * rn01 + ms[M21] * rn02;
        double nms02 = ms[M02] * rn00 + ms[M12] * rn01 + ms[M22] * rn02;
        double nms03 = ms[M03] * rn00 + ms[M13] * rn01 + ms[M23] * rn02;
        double nms10 = ms[M00] * rn10 + ms[M10] * rn11 + ms[M20] * rn12;
        double nms11 = ms[M01] * rn10 + ms[M11] * rn11 + ms[M21] * rn12;
        double nms12 = ms[M02] * rn10 + ms[M12] * rn11 + ms[M22] * rn12;
        double nms13 = ms[M03] * rn10 + ms[M13] * rn11 + ms[M23] * rn12;
        dest.ms[M20] = ms[M00] * rn20 + ms[M10] * rn21 + ms[M20] * rn22;
        dest.ms[M21] = ms[M01] * rn20 + ms[M11] * rn21 + ms[M21] * rn22;
        dest.ms[M22] = ms[M02] * rn20 + ms[M12] * rn21 + ms[M22] * rn22;
        dest.ms[M23] = ms[M03] * rn20 + ms[M13] * rn21 + ms[M23] * rn22;
        // set the rest of the matrix elements
        dest.ms[M00] = nms00;
        dest.ms[M01] = nms01;
        dest.ms[M02] = nms02;
        dest.ms[M03] = nms03;
        dest.ms[M10] = nms10;
        dest.ms[M11] = nms11;
        dest.ms[M12] = nms12;
        dest.ms[M13] = nms13;

        return dest;
    }

    /**
     * Apply a "lookat" transformation to this matrix for a right-handed coordinate system, 
     * that aligns <code>-z</code> with <code>center - eye</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>L</code> the lookat matrix,
     * then the new matrix will be <code>M * L</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * L * v</code>,
     * the lookat transformation will be applied first!
     * <p>
     * In order to set the matrix to a lookat transformation without post-multiplying it,
     * use {@link #setLookAt(double, double, double, double, double, double, double, double, double) setLookAt()}.
     * 
     * @see #lookAt(Vector3d, Vector3d, Vector3d)
     * @see #setLookAt(double, double, double, double, double, double, double, double, double)
     * 
     * @param eyeX
     *              the x-coordinate of the eye/camera location
     * @param eyeY
     *              the y-coordinate of the eye/camera location
     * @param eyeZ
     *              the z-coordinate of the eye/camera location
     * @param centerX
     *              the x-coordinate of the point to look at
     * @param centerY
     *              the y-coordinate of the point to look at
     * @param centerZ
     *              the z-coordinate of the point to look at
     * @param upX
     *              the x-coordinate of the up vector
     * @param upY
     *              the y-coordinate of the up vector
     * @param upZ
     *              the z-coordinate of the up vector
     * @return this
     */
    public Matrix4d lookAt(double eyeX, double eyeY, double eyeZ,
                           double centerX, double centerY, double centerZ,
                           double upX, double upY, double upZ) {
        return lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ, this);
    }

    /**
     * Apply a symmetric perspective projection frustum transformation using the given NDC z range to this matrix and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>P</code> the perspective projection matrix,
     * then the new matrix will be <code>M * P</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * P * v</code>,
     * the perspective projection will be applied first!
     * <p>
     * In order to set the matrix to a perspective frustum transformation without post-multiplying,
     * use {@link #setPerspective(double, double, double, double) setPerspective}.
     * 
     * @see #setPerspective(double, double, double, double)
     * 
     * @param fovy
     *            the vertical field of view in radians (must be greater than zero and less than {@link Math#PI PI})
     * @param aspect
     *            the aspect ratio (i.e. width / height; must be greater than zero)
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param dest
     *            will hold the result
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @return dest
     */
    public Matrix4d perspective(double fovy, double aspect, double zNear, double zFar, boolean zZeroToOne, Matrix4d dest) {
        double h = Math.tan(fovy * 0.5);

        // calculate right matrix elements
        double rn00 = 1.0 / (h * aspect);
        double rn11 = 1.0 / h;
        double rn22;
        double rn32;
        boolean farInf = zFar > 0 && Double.isInfinite(zFar);
        boolean nearInf = zNear > 0 && Double.isInfinite(zNear);
        if (farInf) {
            // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
            double e = 1E-6;
            rn22 = e - 1.0;
            rn32 = (e - (zZeroToOne ? 1.0 : 2.0)) * zNear;
        } else if (nearInf) {
            double e = 1E-6;
            rn22 = (zZeroToOne ? 0.0 : 1.0) - e;
            rn32 = ((zZeroToOne ? 1.0 : 2.0) - e) * zFar;
        } else {
            rn22 = (zZeroToOne ? zFar : zFar + zNear) / (zNear - zFar);
            rn32 = (zZeroToOne ? zFar : zFar + zFar) * zNear / (zNear - zFar);
        }
        // perform optimized matrix multiplication
        double nms20 = ms[M20] * rn22 - ms[M30];
        double nms21 = ms[M21] * rn22 - ms[M31];
        double nms22 = ms[M22] * rn22 - ms[M32];
        double nms23 = ms[M23] * rn22 - ms[M33];
        dest.ms[M00] = ms[M00] * rn00;
        dest.ms[M01] = ms[M01] * rn00;
        dest.ms[M02] = ms[M02] * rn00;
        dest.ms[M03] = ms[M03] * rn00;
        dest.ms[M10] = ms[M10] * rn11;
        dest.ms[M11] = ms[M11] * rn11;
        dest.ms[M12] = ms[M12] * rn11;
        dest.ms[M13] = ms[M13] * rn11;
        dest.ms[M30] = ms[M20] * rn32;
        dest.ms[M31] = ms[M21] * rn32;
        dest.ms[M32] = ms[M22] * rn32;
        dest.ms[M33] = ms[M23] * rn32;
        dest.ms[M20] = nms20;
        dest.ms[M21] = nms21;
        dest.ms[M22] = nms22;
        dest.ms[M23] = nms23;

        return dest;
    }

    /**
     * Apply a symmetric perspective projection frustum transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>P</code> the perspective projection matrix,
     * then the new matrix will be <code>M * P</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * P * v</code>,
     * the perspective projection will be applied first!
     * <p>
     * In order to set the matrix to a perspective frustum transformation without post-multiplying,
     * use {@link #setPerspective(double, double, double, double) setPerspective}.
     * 
     * @see #setPerspective(double, double, double, double)
     * 
     * @param fovy
     *            the vertical field of view in radians (must be greater than zero and less than {@link Math#PI PI})
     * @param aspect
     *            the aspect ratio (i.e. width / height; must be greater than zero)
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d perspective(double fovy, double aspect, double zNear, double zFar, Matrix4d dest) {
        return perspective(fovy, aspect, zNear, zFar, false, dest);
    }

    /**
     * Apply a symmetric perspective projection frustum transformation using the given NDC z range to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>P</code> the perspective projection matrix,
     * then the new matrix will be <code>M * P</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * P * v</code>,
     * the perspective projection will be applied first!
     * <p>
     * In order to set the matrix to a perspective frustum transformation without post-multiplying,
     * use {@link #setPerspective(double, double, double, double, boolean) setPerspective}.
     * 
     * @see #setPerspective(double, double, double, double, boolean)
     * 
     * @param fovy
     *            the vertical field of view in radians (must be greater than zero and less than {@link Math#PI PI})
     * @param aspect
     *            the aspect ratio (i.e. width / height; must be greater than zero)
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @return this
     */
    public Matrix4d perspective(double fovy, double aspect, double zNear, double zFar, boolean zZeroToOne) {
        return perspective(fovy, aspect, zNear, zFar, zZeroToOne, this);
    }

    /**
     * Apply a symmetric perspective projection frustum transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>P</code> the perspective projection matrix,
     * then the new matrix will be <code>M * P</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * P * v</code>,
     * the perspective projection will be applied first!
     * <p>
     * In order to set the matrix to a perspective frustum transformation without post-multiplying,
     * use {@link #setPerspective(double, double, double, double) setPerspective}.
     * 
     * @see #setPerspective(double, double, double, double)
     * 
     * @param fovy
     *            the vertical field of view in radians (must be greater than zero and less than {@link Math#PI PI})
     * @param aspect
     *            the aspect ratio (i.e. width / height; must be greater than zero)
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @return this
     */
    public Matrix4d perspective(double fovy, double aspect, double zNear, double zFar) {
        return perspective(fovy, aspect, zNear, zFar, this);
    }

    /**
     * Set this matrix to be a symmetric perspective projection frustum transformation using the given NDC z range.
     * <p>
     * In order to apply the perspective projection transformation to an existing transformation,
     * use {@link #perspective(double, double, double, double, boolean) perspective()}.
     * 
     * @see #perspective(double, double, double, double, boolean)
     * 
     * @param fovy
     *            the vertical field of view in radians (must be greater than zero and less than {@link Math#PI PI})
     * @param aspect
     *            the aspect ratio (i.e. width / height; must be greater than zero)
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @return this
     */
    public Matrix4d setPerspective(double fovy, double aspect, double zNear, double zFar, boolean zZeroToOne) {
        double h = Math.tan(fovy * 0.5);
        ms[M00] = 1.0 / (h * aspect);
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = 1.0 / h;
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = 0.0;
        ms[M21] = 0.0;
        boolean farInf = zFar > 0 && Double.isInfinite(zFar);
        boolean nearInf = zNear > 0 && Double.isInfinite(zNear);
        if (farInf) {
            // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
            double e = 1E-6;
            ms[M22] = e - 1.0;
            ms[M32] = (e - (zZeroToOne ? 1.0 : 2.0)) * zNear;
        } else if (nearInf) {
            double e = 1E-6;
            ms[M22] = (zZeroToOne ? 0.0 : 1.0) - e;
            ms[M32] = ((zZeroToOne ? 1.0 : 2.0) - e) * zFar;
        } else {
            ms[M22] = (zZeroToOne ? zFar : zFar + zNear) / (zNear - zFar);
            ms[M32] = (zZeroToOne ? zFar : zFar + zFar) * zNear / (zNear - zFar);
        }
        ms[M23] = -1.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M33] = 0.0;
        return this;
    }

    /**
     * Set this matrix to be a symmetric perspective projection frustum transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
     * <p>
     * In order to apply the perspective projection transformation to an existing transformation,
     * use {@link #perspective(double, double, double, double) perspective()}.
     * 
     * @see #perspective(double, double, double, double)
     * 
     * @param fovy
     *            the vertical field of view in radians (must be greater than zero and less than {@link Math#PI PI})
     * @param aspect
     *            the aspect ratio (i.e. width / height; must be greater than zero)
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @return this
     */
    public Matrix4d setPerspective(double fovy, double aspect, double zNear, double zFar) {
        return setPerspective(fovy, aspect, zNear, zFar, false);
    }

    /**
     * Apply an arbitrary perspective projection frustum transformation using the given NDC z range to this matrix 
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>F</code> the frustum matrix,
     * then the new matrix will be <code>M * F</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * F * v</code>,
     * the frustum transformation will be applied first!
     * <p>
     * In order to set the matrix to a perspective frustum transformation without post-multiplying,
     * use {@link #setFrustum(double, double, double, double, double, double, boolean) setFrustum()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective">http://www.songho.ca</a>
     * 
     * @see #setFrustum(double, double, double, double, double, double, boolean)
     * 
     * @param left
     *            the distance along the x-axis to the left frustum edge
     * @param right
     *            the distance along the x-axis to the right frustum edge
     * @param bottom
     *            the distance along the y-axis to the bottom frustum edge
     * @param top
     *            the distance along the y-axis to the top frustum edge
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d frustum(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne, Matrix4d dest) {
        // calculate right matrix elements
        double rn00 = (zNear + zNear) / (right - left);
        double rn11 = (zNear + zNear) / (top - bottom);
        double rn20 = (right + left) / (right - left);
        double rn21 = (top + bottom) / (top - bottom);
        double rn22;
        double rn32;
        boolean farInf = zFar > 0 && Double.isInfinite(zFar);
        boolean nearInf = zNear > 0 && Double.isInfinite(zNear);
        if (farInf) {
            // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
            double e = 1E-6;
            rn22 = e - 1.0;
            rn32 = (e - (zZeroToOne ? 1.0 : 2.0)) * zNear;
        } else if (nearInf) {
            double e = 1E-6;
            rn22 = (zZeroToOne ? 0.0 : 1.0) - e;
            rn32 = ((zZeroToOne ? 1.0 : 2.0) - e) * zFar;
        } else {
            rn22 = (zZeroToOne ? zFar : zFar + zNear) / (zNear - zFar);
            rn32 = (zZeroToOne ? zFar : zFar + zFar) * zNear / (zNear - zFar);
        }
        // perform optimized matrix multiplication
        double nms20 = ms[M00] * rn20 + ms[M10] * rn21 + ms[M20] * rn22 - ms[M30];
        double nms21 = ms[M01] * rn20 + ms[M11] * rn21 + ms[M21] * rn22 - ms[M31];
        double nms22 = ms[M02] * rn20 + ms[M12] * rn21 + ms[M22] * rn22 - ms[M32];
        double nms23 = ms[M03] * rn20 + ms[M13] * rn21 + ms[M23] * rn22 - ms[M33];
        dest.ms[M00] = ms[M00] * rn00;
        dest.ms[M01] = ms[M01] * rn00;
        dest.ms[M02] = ms[M02] * rn00;
        dest.ms[M03] = ms[M03] * rn00;
        dest.ms[M10] = ms[M10] * rn11;
        dest.ms[M11] = ms[M11] * rn11;
        dest.ms[M12] = ms[M12] * rn11;
        dest.ms[M13] = ms[M13] * rn11;
        dest.ms[M30] = ms[M20] * rn32;
        dest.ms[M31] = ms[M21] * rn32;
        dest.ms[M32] = ms[M22] * rn32;
        dest.ms[M33] = ms[M23] * rn32;
        dest.ms[M20] = nms20;
        dest.ms[M21] = nms21;
        dest.ms[M22] = nms22;
        dest.ms[M23] = nms23;
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = ms[M32];
        dest.ms[M33] = ms[M33];

        return dest;
    }

    /**
     * Apply an arbitrary perspective projection frustum transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix 
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>F</code> the frustum matrix,
     * then the new matrix will be <code>M * F</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * F * v</code>,
     * the frustum transformation will be applied first!
     * <p>
     * In order to set the matrix to a perspective frustum transformation without post-multiplying,
     * use {@link #setFrustum(double, double, double, double, double, double) setFrustum()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective">http://www.songho.ca</a>
     * 
     * @see #setFrustum(double, double, double, double, double, double)
     * 
     * @param left
     *            the distance along the x-axis to the left frustum edge
     * @param right
     *            the distance along the x-axis to the right frustum edge
     * @param bottom
     *            the distance along the y-axis to the bottom frustum edge
     * @param top
     *            the distance along the y-axis to the top frustum edge
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param dest
     *            will hold the result
     * @return dest
     */
    public Matrix4d frustum(double left, double right, double bottom, double top, double zNear, double zFar, Matrix4d dest) {
        return frustum(left, right, bottom, top, zNear, zFar, false, dest);
    }

    /**
     * Apply an arbitrary perspective projection frustum transformation using the given NDC z range to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>F</code> the frustum matrix,
     * then the new matrix will be <code>M * F</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * F * v</code>,
     * the frustum transformation will be applied first!
     * <p>
     * In order to set the matrix to a perspective frustum transformation without post-multiplying,
     * use {@link #setFrustum(double, double, double, double, double, double, boolean) setFrustum()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective">http://www.songho.ca</a>
     * 
     * @see #setFrustum(double, double, double, double, double, double, boolean)
     * 
     * @param left
     *            the distance along the x-axis to the left frustum edge
     * @param right
     *            the distance along the x-axis to the right frustum edge
     * @param bottom
     *            the distance along the y-axis to the bottom frustum edge
     * @param top
     *            the distance along the y-axis to the top frustum edge
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @return this
     */
    public Matrix4d frustum(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne) {
        return frustum(left, right, bottom, top, zNear, zFar, zZeroToOne, this);
    }

    /**
     * Apply an arbitrary perspective projection frustum transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt> to this matrix.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>F</code> the frustum matrix,
     * then the new matrix will be <code>M * F</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * F * v</code>,
     * the frustum transformation will be applied first!
     * <p>
     * In order to set the matrix to a perspective frustum transformation without post-multiplying,
     * use {@link #setFrustum(double, double, double, double, double, double) setFrustum()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective">http://www.songho.ca</a>
     * 
     * @see #setFrustum(double, double, double, double, double, double)
     * 
     * @param left
     *            the distance along the x-axis to the left frustum edge
     * @param right
     *            the distance along the x-axis to the right frustum edge
     * @param bottom
     *            the distance along the y-axis to the bottom frustum edge
     * @param top
     *            the distance along the y-axis to the top frustum edge
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @return this
     */
    public Matrix4d frustum(double left, double right, double bottom, double top, double zNear, double zFar) {
        return frustum(left, right, bottom, top, zNear, zFar, this);
    }

    /**
     * Set this matrix to be an arbitrary perspective projection frustum transformation using the given NDC z range.
     * <p>
     * In order to apply the perspective frustum transformation to an existing transformation,
     * use {@link #frustum(double, double, double, double, double, double, boolean) frustum()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective">http://www.songho.ca</a>
     * 
     * @see #frustum(double, double, double, double, double, double, boolean)
     * 
     * @param left
     *            the distance along the x-axis to the left frustum edge
     * @param right
     *            the distance along the x-axis to the right frustum edge
     * @param bottom
     *            the distance along the y-axis to the bottom frustum edge
     * @param top
     *            the distance along the y-axis to the top frustum edge
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zZeroToOne
     *            whether to use Vulkan's and Direct3D's NDC z range of <tt>[0..+1]</tt> when <code>true</code>
     *            or whether to use OpenGL's NDC z range of <tt>[-1..+1]</tt> when <code>false</code>
     * @return this
     */
    public Matrix4d setFrustum(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne) {
        ms[M00] = (zNear + zNear) / (right - left);
        ms[M01] = 0.0;
        ms[M02] = 0.0;
        ms[M03] = 0.0;
        ms[M10] = 0.0;
        ms[M11] = (zNear + zNear) / (top - bottom);
        ms[M12] = 0.0;
        ms[M13] = 0.0;
        ms[M20] = (right + left) / (right - left);
        ms[M21] = (top + bottom) / (top - bottom);
        boolean farInf = zFar > 0 && Double.isInfinite(zFar);
        boolean nearInf = zNear > 0 && Double.isInfinite(zNear);
        if (farInf) {
            // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
            double e = 1E-6;
            ms[M22] = e - 1.0;
            ms[M32] = (e - (zZeroToOne ? 1.0 : 2.0)) * zNear;
        } else if (nearInf) {
            double e = 1E-6;
            ms[M22] = (zZeroToOne ? 0.0 : 1.0) - e;
            ms[M32] = ((zZeroToOne ? 1.0 : 2.0) - e) * zFar;
        } else {
            ms[M22] = (zZeroToOne ? zFar : zFar + zNear) / (zNear - zFar);
            ms[M32] = (zZeroToOne ? zFar : zFar + zFar) * zNear / (zNear - zFar);
        }
        ms[M23] = -1.0;
        ms[M30] = 0.0;
        ms[M31] = 0.0;
        ms[M33] = 0.0;
        return this;
    }

    /**
     * Set this matrix to be an arbitrary perspective projection frustum transformation using OpenGL's NDC z range of <tt>[-1..+1]</tt>.
     * <p>
     * In order to apply the perspective frustum transformation to an existing transformation,
     * use {@link #frustum(double, double, double, double, double, double) frustum()}.
     * <p>
     * Reference: <a href="http://www.songho.ca/opengl/gl_projectionmatrix.html#perspective">http://www.songho.ca</a>
     * 
     * @see #frustum(double, double, double, double, double, double)
     * 
     * @param left
     *            the distance along the x-axis to the left frustum edge
     * @param right
     *            the distance along the x-axis to the right frustum edge
     * @param bottom
     *            the distance along the y-axis to the bottom frustum edge
     * @param top
     *            the distance along the y-axis to the top frustum edge
     * @param zNear
     *            near clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. If the special value {@link Double#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Double#POSITIVE_INFINITY}.
     * @return this
     */
    public Matrix4d setFrustum(double left, double right, double bottom, double top, double zNear, double zFar) {
        return setFrustum(left, right, bottom, top, zNear, zFar, false);
    }

    /**
     * Calculate a frustum plane of the this matrix, which
     * can be a projection matrix or a combined modelview-projection matrix, and store the result
     * in the given <code>planeEquation</code>.
     * <p>
     * Generally, this method computes the frustum plane in the local frame of
     * any coordinate system that existed before <code>this</code>
     * transformation was applied to it in order to yield homogeneous clipping space.
     * <p>
     * The frustum plane will be given in the form of a general plane equation:
     * <tt>a*x + b*y + c*z + d = 0</tt>, where the given {@link Vector4d} components will
     * hold the <tt>(a, b, c, d)</tt> values of the equation.
     * <p>
     * The plane normal, which is <tt>(a, b, c)</tt>, is directed "inwards" of the frustum.
     * Any plane/point test using <tt>a*x + b*y + c*z + d</tt> therefore will yield a result greater than zero
     * if the point is within the frustum (i.e. at the <i>positive</i> side of the frustum plane).
     * <p>
     * Reference: <a href="http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf">
     * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix</a>
     *
     * @param plane
     *          one of the six possible planes, given as numeric constants
     *          {@link #PLANE_NX}, {@link #PLANE_PX},
     *          {@link #PLANE_NY}, {@link #PLANE_PY}, 
     *          {@link #PLANE_NZ} and {@link #PLANE_PZ}
     * @param planeEquation
     *          will hold the computed plane equation.
     *          The plane equation will be normalized, meaning that <tt>(a, b, c)</tt> will be a unit vector
     * @return planeEquation
     */
    public Vector4d frustumPlane(int plane, Vector4d planeEquation) {
        switch (plane) {
        case PLANE_NX:
            planeEquation.set(ms[M03] + ms[M00], ms[M13] + ms[M10], ms[M23] + ms[M20], ms[M33] + ms[M30]).normalize3();
            break;
        case PLANE_PX:
            planeEquation.set(ms[M03] - ms[M00], ms[M13] - ms[M10], ms[M23] - ms[M20], ms[M33] - ms[M30]).normalize3();
            break;
        case PLANE_NY:
            planeEquation.set(ms[M03] + ms[M01], ms[M13] + ms[M11], ms[M23] + ms[M21], ms[M33] + ms[M31]).normalize3();
            break;
        case PLANE_PY:
            planeEquation.set(ms[M03] - ms[M01], ms[M13] - ms[M11], ms[M23] - ms[M21], ms[M33] - ms[M31]).normalize3();
            break;
        case PLANE_NZ:
            planeEquation.set(ms[M03] + ms[M02], ms[M13] + ms[M12], ms[M23] + ms[M22], ms[M33] + ms[M32]).normalize3();
            break;
        case PLANE_PZ:
            planeEquation.set(ms[M03] - ms[M02], ms[M13] - ms[M12], ms[M23] - ms[M22], ms[M33] - ms[M32]).normalize3();
            break;
        default:
            throw new IllegalArgumentException("plane"); //$NON-NLS-1$
        }
        return planeEquation;
    }

    /**
     * Compute the corner coordinates of the frustum defined by <code>this</code> matrix, which
     * can be a projection matrix or a combined modelview-projection matrix, and store the result
     * in the given <code>point</code>.
     * <p>
     * Generally, this method computes the frustum corners in the local frame of
     * any coordinate system that existed before <code>this</code>
     * transformation was applied to it in order to yield homogeneous clipping space.
     * <p>
     * Reference: <a href="http://geomalgorithms.com/a05-_intersect-1.html">http://geomalgorithms.com</a>
     * <p>
     * Reference: <a href="http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf">
     * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix</a>
     * 
     * @param corner
     *          one of the eight possible corners, given as numeric constants
     *          {@link #CORNER_NXNYNZ}, {@link #CORNER_PXNYNZ}, {@link #CORNER_PXPYNZ}, {@link #CORNER_NXPYNZ},
     *          {@link #CORNER_PXNYPZ}, {@link #CORNER_NXNYPZ}, {@link #CORNER_NXPYPZ}, {@link #CORNER_PXPYPZ}
     * @param point
     *          will hold the resulting corner point coordinates
     * @return point
     */
    public Vector3d frustumCorner(int corner, Vector3d point) {
        double d1, d2, d3;
        double n1x, n1y, n1z, n2x, n2y, n2z, n3x, n3y, n3z;
        switch (corner) {
        case CORNER_NXNYNZ: // left, bottom, near
            n1x = ms[M03] + ms[M00]; n1y = ms[M13] + ms[M10]; n1z = ms[M23] + ms[M20]; d1 = ms[M33] + ms[M30]; // left
            n2x = ms[M03] + ms[M01]; n2y = ms[M13] + ms[M11]; n2z = ms[M23] + ms[M21]; d2 = ms[M33] + ms[M31]; // bottom
            n3x = ms[M03] + ms[M02]; n3y = ms[M13] + ms[M12]; n3z = ms[M23] + ms[M22]; d3 = ms[M33] + ms[M32]; // near
            break;
        case CORNER_PXNYNZ: // right, bottom, near
            n1x = ms[M03] - ms[M00]; n1y = ms[M13] - ms[M10]; n1z = ms[M23] - ms[M20]; d1 = ms[M33] - ms[M30]; // right
            n2x = ms[M03] + ms[M01]; n2y = ms[M13] + ms[M11]; n2z = ms[M23] + ms[M21]; d2 = ms[M33] + ms[M31]; // bottom
            n3x = ms[M03] + ms[M02]; n3y = ms[M13] + ms[M12]; n3z = ms[M23] + ms[M22]; d3 = ms[M33] + ms[M32]; // near
            break;
        case CORNER_PXPYNZ: // right, top, near
            n1x = ms[M03] - ms[M00]; n1y = ms[M13] - ms[M10]; n1z = ms[M23] - ms[M20]; d1 = ms[M33] - ms[M30]; // right
            n2x = ms[M03] - ms[M01]; n2y = ms[M13] - ms[M11]; n2z = ms[M23] - ms[M21]; d2 = ms[M33] - ms[M31]; // top
            n3x = ms[M03] + ms[M02]; n3y = ms[M13] + ms[M12]; n3z = ms[M23] + ms[M22]; d3 = ms[M33] + ms[M32]; // near
            break;
        case CORNER_NXPYNZ: // left, top, near
            n1x = ms[M03] + ms[M00]; n1y = ms[M13] + ms[M10]; n1z = ms[M23] + ms[M20]; d1 = ms[M33] + ms[M30]; // left
            n2x = ms[M03] - ms[M01]; n2y = ms[M13] - ms[M11]; n2z = ms[M23] - ms[M21]; d2 = ms[M33] - ms[M31]; // top
            n3x = ms[M03] + ms[M02]; n3y = ms[M13] + ms[M12]; n3z = ms[M23] + ms[M22]; d3 = ms[M33] + ms[M32]; // near
            break;
        case CORNER_PXNYPZ: // right, bottom, far
            n1x = ms[M03] - ms[M00]; n1y = ms[M13] - ms[M10]; n1z = ms[M23] - ms[M20]; d1 = ms[M33] - ms[M30]; // right
            n2x = ms[M03] + ms[M01]; n2y = ms[M13] + ms[M11]; n2z = ms[M23] + ms[M21]; d2 = ms[M33] + ms[M31]; // bottom
            n3x = ms[M03] - ms[M02]; n3y = ms[M13] - ms[M12]; n3z = ms[M23] - ms[M22]; d3 = ms[M33] - ms[M32]; // far
            break;
        case CORNER_NXNYPZ: // left, bottom, far
            n1x = ms[M03] + ms[M00]; n1y = ms[M13] + ms[M10]; n1z = ms[M23] + ms[M20]; d1 = ms[M33] + ms[M30]; // left
            n2x = ms[M03] + ms[M01]; n2y = ms[M13] + ms[M11]; n2z = ms[M23] + ms[M21]; d2 = ms[M33] + ms[M31]; // bottom
            n3x = ms[M03] - ms[M02]; n3y = ms[M13] - ms[M12]; n3z = ms[M23] - ms[M22]; d3 = ms[M33] - ms[M32]; // far
            break;
        case CORNER_NXPYPZ: // left, top, far
            n1x = ms[M03] + ms[M00]; n1y = ms[M13] + ms[M10]; n1z = ms[M23] + ms[M20]; d1 = ms[M33] + ms[M30]; // left
            n2x = ms[M03] - ms[M01]; n2y = ms[M13] - ms[M11]; n2z = ms[M23] - ms[M21]; d2 = ms[M33] - ms[M31]; // top
            n3x = ms[M03] - ms[M02]; n3y = ms[M13] - ms[M12]; n3z = ms[M23] - ms[M22]; d3 = ms[M33] - ms[M32]; // far
            break;
        case CORNER_PXPYPZ: // right, top, far
            n1x = ms[M03] - ms[M00]; n1y = ms[M13] - ms[M10]; n1z = ms[M23] - ms[M20]; d1 = ms[M33] - ms[M30]; // right
            n2x = ms[M03] - ms[M01]; n2y = ms[M13] - ms[M11]; n2z = ms[M23] - ms[M21]; d2 = ms[M33] - ms[M31]; // top
            n3x = ms[M03] - ms[M02]; n3y = ms[M13] - ms[M12]; n3z = ms[M23] - ms[M22]; d3 = ms[M33] - ms[M32]; // far
            break;
        default:
            throw new IllegalArgumentException("corner"); //$NON-NLS-1$
        }
        double c23x, c23y, c23z;
        c23x = n2y * n3z - n2z * n3y;
        c23y = n2z * n3x - n2x * n3z;
        c23z = n2x * n3y - n2y * n3x;
        double c31x, c31y, c31z;
        c31x = n3y * n1z - n3z * n1y;
        c31y = n3z * n1x - n3x * n1z;
        c31z = n3x * n1y - n3y * n1x;
        double c12x, c12y, c12z;
        c12x = n1y * n2z - n1z * n2y;
        c12y = n1z * n2x - n1x * n2z;
        c12z = n1x * n2y - n1y * n2x;
        double invDot = 1.0 / (n1x * c23x + n1y * c23y + n1z * c23z);
        point.x = (-c23x * d1 - c31x * d2 - c12x * d3) * invDot;
        point.y = (-c23y * d1 - c31y * d2 - c12y * d3) * invDot;
        point.z = (-c23z * d1 - c31z * d2 - c12z * d3) * invDot;
        return point;
    }

    /**
     * Compute the eye/origin of the perspective frustum transformation defined by <code>this</code> matrix, 
     * which can be a projection matrix or a combined modelview-projection matrix, and store the result
     * in the given <code>origin</code>.
     * <p>
     * Note that this method will only work using perspective projections obtained via one of the
     * perspective methods, such as {@link #perspective(double, double, double, double) perspective()}
     * or {@link #frustum(double, double, double, double, double, double) frustum()}.
     * <p>
     * Generally, this method computes the origin in the local frame of
     * any coordinate system that existed before <code>this</code>
     * transformation was applied to it in order to yield homogeneous clipping space.
     * <p>
     * Reference: <a href="http://geomalgorithms.com/a05-_intersect-1.html">http://geomalgorithms.com</a>
     * <p>
     * Reference: <a href="http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf">
     * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix</a>
     * 
     * @param origin
     *          will hold the origin of the coordinate system before applying <code>this</code>
     *          perspective projection transformation
     * @return origin
     */
    public Vector3d perspectiveOrigin(Vector3d origin) {
        /*
         * Simply compute the intersection point of the left, right and top frustum plane.
         */
        double d1, d2, d3;
        double n1x, n1y, n1z, n2x, n2y, n2z, n3x, n3y, n3z;
        n1x = ms[M03] + ms[M00]; n1y = ms[M13] + ms[M10]; n1z = ms[M23] + ms[M20]; d1 = ms[M33] + ms[M30]; // left
        n2x = ms[M03] - ms[M00]; n2y = ms[M13] - ms[M10]; n2z = ms[M23] - ms[M20]; d2 = ms[M33] - ms[M30]; // right
        n3x = ms[M03] - ms[M01]; n3y = ms[M13] - ms[M11]; n3z = ms[M23] - ms[M21]; d3 = ms[M33] - ms[M31]; // top
        double c23x, c23y, c23z;
        c23x = n2y * n3z - n2z * n3y;
        c23y = n2z * n3x - n2x * n3z;
        c23z = n2x * n3y - n2y * n3x;
        double c31x, c31y, c31z;
        c31x = n3y * n1z - n3z * n1y;
        c31y = n3z * n1x - n3x * n1z;
        c31z = n3x * n1y - n3y * n1x;
        double c12x, c12y, c12z;
        c12x = n1y * n2z - n1z * n2y;
        c12y = n1z * n2x - n1x * n2z;
        c12z = n1x * n2y - n1y * n2x;
        double invDot = 1.0 / (n1x * c23x + n1y * c23y + n1z * c23z);
        origin.x = (-c23x * d1 - c31x * d2 - c12x * d3) * invDot;
        origin.y = (-c23y * d1 - c31y * d2 - c12y * d3) * invDot;
        origin.z = (-c23z * d1 - c31z * d2 - c12z * d3) * invDot;
        return origin;
    }

    /**
     * Return the vertical field-of-view angle in radians of this perspective transformation matrix.
     * <p>
     * Note that this method will only work using perspective projections obtained via one of the
     * perspective methods, such as {@link #perspective(double, double, double, double) perspective()}
     * or {@link #frustum(double, double, double, double, double, double) frustum()}.
     * <p>
     * For orthogonal transformations this method will return <tt>0.0</tt>.
     * <p>
     * Reference: <a href="http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf">
     * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix</a>
     * 
     * @return the vertical field-of-view angle in radians
     */
    public double perspectiveFov() {
        /*
         * Compute the angle between the bottom and top frustum plane normals.
         */
        double n1x, n1y, n1z, n2x, n2y, n2z;
        n1x = ms[M03] + ms[M01]; n1y = ms[M13] + ms[M11]; n1z = ms[M23] + ms[M21]; // bottom
        n2x = ms[M01] - ms[M03]; n2y = ms[M11] - ms[M13]; n2z = ms[M21] - ms[M23]; // top
        double n1len = Math.sqrt(n1x * n1x + n1y * n1y + n1z * n1z);
        double n2len = Math.sqrt(n2x * n2x + n2y * n2y + n2z * n2z);
        return Math.acos((n1x * n2x + n1y * n2y + n1z * n2z) / (n1len * n2len));
    }

    /**
     * Extract the near clip plane distance from <code>this</code> perspective projection matrix.
     * <p>
     * This method only works if <code>this</code> is a perspective projection matrix, for example obtained via {@link #perspective(double, double, double, double)}.
     * 
     * @return the near clip plane distance
     */
    public double perspectiveNear() {
        return ms[M32] / (ms[M23] + ms[M22]);
    }

    /**
     * Extract the far clip plane distance from <code>this</code> perspective projection matrix.
     * <p>
     * This method only works if <code>this</code> is a perspective projection matrix, for example obtained via {@link #perspective(double, double, double, double)}.
     * 
     * @return the far clip plane distance
     */
    public double perspectiveFar() {
        return ms[M32] / (ms[M22] - ms[M23]);
    }

    /**
     * Obtain the direction of a ray starting at the center of the coordinate system and going
     * through the near frustum plane.
     * <p>
     * This method computes the <code>dir</code> vector in the local frame of
     * any coordinate system that existed before <code>this</code>
     * transformation was applied to it in order to yield homogeneous clipping space.
     * <p>
     * The parameters <code>x</code> and <code>y</code> are used to interpolate the generated ray direction
     * from the bottom-left to the top-right frustum corners.
     * <p>
     * For optimal efficiency when building many ray directions over the whole frustum,
     * it is recommended to use this method only in order to compute the four corner rays at
     * <tt>(0, 0)</tt>, <tt>(1, 0)</tt>, <tt>(0, 1)</tt> and <tt>(1, 1)</tt>
     * and then bilinearly interpolating between them; or to use the {@link FrustumRayBuilder}.
     * <p>
     * Reference: <a href="http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf">
     * Fast Extraction of Viewing Frustum Planes from the World-View-Projection Matrix</a>
     * 
     * @param x
     *          the interpolation factor along the left-to-right frustum planes, within <tt>[0..1]</tt>
     * @param y
     *          the interpolation factor along the bottom-to-top frustum planes, within <tt>[0..1]</tt>
     * @param dir
     *          will hold the normalized ray direction in the local frame of the coordinate system before 
     *          transforming to homogeneous clipping space using <code>this</code> matrix
     * @return dir
     */
    public Vector3d frustumRayDir(double x, double y, Vector3d dir) {
        /*
         * This method works by first obtaining the frustum plane normals,
         * then building the cross product to obtain the corner rays,
         * and finally bilinearly interpolating to obtain the desired direction.
         * The code below uses a condense form of doing all this making use 
         * of some mathematical identities to simplify the overall expression.
         */
        double a = ms[M10] * ms[M23], b = ms[M13] * ms[M21], c = ms[M10] * ms[M21], d = ms[M11] * ms[M23], e = ms[M13] * ms[M20], f = ms[M11] * ms[M20];
        double g = ms[M03] * ms[M20], h = ms[M01] * ms[M23], i = ms[M01] * ms[M20], j = ms[M03] * ms[M21], k = ms[M00] * ms[M23], l = ms[M00] * ms[M21];
        double m = ms[M00] * ms[M13], n = ms[M03] * ms[M11], o = ms[M00] * ms[M11], p = ms[M01] * ms[M13], q = ms[M03] * ms[M10], r = ms[M01] * ms[M10];
        double m1x, m1y, m1z;
        m1x = (d + e + f - a - b - c) * (1.0 - y) + (a - b - c + d - e + f) * y;
        m1y = (j + k + l - g - h - i) * (1.0 - y) + (g - h - i + j - k + l) * y;
        m1z = (p + q + r - m - n - o) * (1.0 - y) + (m - n - o + p - q + r) * y;
        double m2x, m2y, m2z;
        m2x = (b - c - d + e + f - a) * (1.0 - y) + (a + b - c - d - e + f) * y;
        m2y = (h - i - j + k + l - g) * (1.0 - y) + (g + h - i - j - k + l) * y;
        m2z = (n - o - p + q + r - m) * (1.0 - y) + (m + n - o - p - q + r) * y;
        dir.x = m1x * (1.0 - x) + m2x * x;
        dir.y = m1y * (1.0 - x) + m2y * x;
        dir.z = m1z * (1.0 - x) + m2z * x;
        dir.normalize();
        return dir;
    }

    /**
     * Obtain the direction of <tt>+Z</tt> before the transformation represented by <code>this</code> matrix is applied.
     * <p>
     * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction 
     * that is transformed to <tt>+Z</tt> by <code>this</code> matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix4d inv = new Matrix4d(this).invert();
     * inv.transformDirection(dir.set(0, 0, 1)).normalize();
     * </pre>
     * If <code>this</code> is already an orthogonal matrix, then consider using {@link #normalizedPositiveZ(Vector3d)} instead.
     * <p>
     * Reference: <a href="http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/">http://www.euclideanspace.com</a>
     * 
     * @param dir
     *          will hold the direction of <tt>+Z</tt>
     * @return dir
     */
    public Vector3d positiveZ(Vector3d dir) {
        dir.x = ms[M10] * ms[M21] - ms[M11] * ms[M20];
        dir.y = ms[M20] * ms[M01] - ms[M21] * ms[M00];
        dir.z = ms[M00] * ms[M11] - ms[M01] * ms[M10];
        dir.normalize();
        return dir;
    }

    /**
     * Obtain the direction of <tt>+Z</tt> before the transformation represented by <code>this</code> <i>orthogonal</i> matrix is applied.
     * This method only produces correct results if <code>this</code> is an <i>orthogonal</i> matrix.
     * <p>
     * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction 
     * that is transformed to <tt>+Z</tt> by <code>this</code> matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix4d inv = new Matrix4d(this).transpose();
     * inv.transformDirection(dir.set(0, 0, 1)).normalize();
     * </pre>
     * <p>
     * Reference: <a href="http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/">http://www.euclideanspace.com</a>
     * 
     * @param dir
     *          will hold the direction of <tt>+Z</tt>
     * @return dir
     */
    public Vector3d normalizedPositiveZ(Vector3d dir) {
        dir.x = ms[M02];
        dir.y = ms[M12];
        dir.z = ms[M22];
        return dir;
    }

    /**
     * Obtain the direction of <tt>+X</tt> before the transformation represented by <code>this</code> matrix is applied.
     * <p>
     * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction 
     * that is transformed to <tt>+X</tt> by <code>this</code> matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix4d inv = new Matrix4d(this).invert();
     * inv.transformDirection(dir.set(1, 0, 0)).normalize();
     * </pre>
     * If <code>this</code> is already an orthogonal matrix, then consider using {@link #normalizedPositiveX(Vector3d)} instead.
     * <p>
     * Reference: <a href="http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/">http://www.euclideanspace.com</a>
     * 
     * @param dir
     *          will hold the direction of <tt>+X</tt>
     * @return dir
     */
    public Vector3d positiveX(Vector3d dir) {
        dir.x = ms[M11] * ms[M22] - ms[M12] * ms[M21];
        dir.y = ms[M02] * ms[M21] - ms[M01] * ms[M22];
        dir.z = ms[M01] * ms[M12] - ms[M02] * ms[M11];
        dir.normalize();
        return dir;
    }

    /**
     * Obtain the direction of <tt>+X</tt> before the transformation represented by <code>this</code> <i>orthogonal</i> matrix is applied.
     * This method only produces correct results if <code>this</code> is an <i>orthogonal</i> matrix.
     * <p>
     * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction 
     * that is transformed to <tt>+X</tt> by <code>this</code> matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix4d inv = new Matrix4d(this).transpose();
     * inv.transformDirection(dir.set(1, 0, 0)).normalize();
     * </pre>
     * <p>
     * Reference: <a href="http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/">http://www.euclideanspace.com</a>
     * 
     * @param dir
     *          will hold the direction of <tt>+X</tt>
     * @return dir
     */
    public Vector3d normalizedPositiveX(Vector3d dir) {
        dir.x = ms[M00];
        dir.y = ms[M10];
        dir.z = ms[M20];
        return dir;
    }

    /**
     * Obtain the direction of <tt>+Y</tt> before the transformation represented by <code>this</code> matrix is applied.
     * <p>
     * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction 
     * that is transformed to <tt>+Y</tt> by <code>this</code> matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix4d inv = new Matrix4d(this).invert();
     * inv.transformDirection(dir.set(0, 1, 0)).normalize();
     * </pre>
     * If <code>this</code> is already an orthogonal matrix, then consider using {@link #normalizedPositiveY(Vector3d)} instead.
     * <p>
     * Reference: <a href="http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/">http://www.euclideanspace.com</a>
     * 
     * @param dir
     *          will hold the direction of <tt>+Y</tt>
     * @return dir
     */
    public Vector3d positiveY(Vector3d dir) {
        dir.x = ms[M12] * ms[M20] - ms[M10] * ms[M22];
        dir.y = ms[M00] * ms[M22] - ms[M02] * ms[M20];
        dir.z = ms[M02] * ms[M10] - ms[M00] * ms[M12];
        dir.normalize();
        return dir;
    }

    /**
     * Obtain the direction of <tt>+Y</tt> before the transformation represented by <code>this</code> <i>orthogonal</i> matrix is applied.
     * This method only produces correct results if <code>this</code> is an <i>orthogonal</i> matrix.
     * <p>
     * This method uses the rotation component of the upper left 3x3 submatrix to obtain the direction 
     * that is transformed to <tt>+Y</tt> by <code>this</code> matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix4d inv = new Matrix4d(this).transpose();
     * inv.transformDirection(dir.set(0, 1, 0)).normalize();
     * </pre>
     * <p>
     * Reference: <a href="http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/">http://www.euclideanspace.com</a>
     * 
     * @param dir
     *          will hold the direction of <tt>+Y</tt>
     * @return dir
     */
    public Vector3d normalizedPositiveY(Vector3d dir) {
        dir.x = ms[M01];
        dir.y = ms[M11];
        dir.z = ms[M21];
        return dir;
    }

    /**
     * Obtain the position that gets transformed to the origin by <code>this</code> {@link #isAffine() affine} matrix.
     * This can be used to get the position of the "camera" from a given <i>view</i> transformation matrix.
     * <p>
     * This method only works with {@link #isAffine() affine} matrices.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix4f inv = new Matrix4f(this).invertAffine();
     * inv.transformPosition(origin.set(0, 0, 0));
     * </pre>
     * 
     * @param origin
     *          will hold the position transformed to the origin
     * @return origin
     */
    public Vector3d originAffine(Vector3d origin) {
        double a = ms[M00] * ms[M11] - ms[M01] * ms[M10];
        double b = ms[M00] * ms[M12] - ms[M02] * ms[M10];
        double d = ms[M01] * ms[M12] - ms[M02] * ms[M11];
        double g = ms[M20] * ms[M31] - ms[M21] * ms[M30];
        double h = ms[M20] * ms[M32] - ms[M22] * ms[M30];
        double j = ms[M21] * ms[M32] - ms[M22] * ms[M31];
        origin.x = -ms[M10] * j + ms[M11] * h - ms[M12] * g;
        origin.y =  ms[M00] * j - ms[M01] * h + ms[M02] * g;
        origin.z = -ms[M30] * d + ms[M31] * b - ms[M32] * a;
        return origin;
    }

    /**
     * Obtain the position that gets transformed to the origin by <code>this</code> matrix.
     * This can be used to get the position of the "camera" from a given <i>view/projection</i> transformation matrix.
     * <p>
     * This method is equivalent to the following code:
     * <pre>
     * Matrix4f inv = new Matrix4f(this).invert();
     * inv.transformPosition(origin.set(0, 0, 0));
     * </pre>
     * 
     * @param origin
     *          will hold the position transformed to the origin
     * @return origin
     */
    public Vector3d origin(Vector3d origin) {
        double a = ms[M00] * ms[M11] - ms[M01] * ms[M10];
        double b = ms[M00] * ms[M12] - ms[M02] * ms[M10];
        double c = ms[M00] * ms[M13] - ms[M03] * ms[M10];
        double d = ms[M01] * ms[M12] - ms[M02] * ms[M11];
        double e = ms[M01] * ms[M13] - ms[M03] * ms[M11];
        double f = ms[M02] * ms[M13] - ms[M03] * ms[M12];
        double g = ms[M20] * ms[M31] - ms[M21] * ms[M30];
        double h = ms[M20] * ms[M32] - ms[M22] * ms[M30];
        double i = ms[M20] * ms[M33] - ms[M23] * ms[M30];
        double j = ms[M21] * ms[M32] - ms[M22] * ms[M31];
        double k = ms[M21] * ms[M33] - ms[M23] * ms[M31];
        double l = ms[M22] * ms[M33] - ms[M23] * ms[M32];
        double det = a * l - b * k + c * j + d * i - e * h + f * g;
        double invDet = 1.0 / det;
        double nms30 = (-ms[M10] * j + ms[M11] * h - ms[M12] * g) * invDet;
        double nms31 = ( ms[M00] * j - ms[M01] * h + ms[M02] * g) * invDet;
        double nms32 = (-ms[M30] * d + ms[M31] * b - ms[M32] * a) * invDet;
        double nms33 = det / ( ms[M20] * d - ms[M21] * b + ms[M22] * a);
        double x = nms30 * nms33;
        double y = nms31 * nms33;
        double z = nms32 * nms33;
        return origin.set(x, y, z);
    }

    /**
     * Apply a projection transformation to this matrix that projects onto the plane specified via the general plane equation
     * <tt>x*a + y*b + z*c + d = 0</tt> as if casting a shadow from a given light position/direction <code>light</code>.
     * <p>
     * If <tt>light.w</tt> is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the shadow matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * reflection will be applied first!
     * <p>
     * Reference: <a href="ftp://ftp.sgi.com/opengl/contrib/blythe/advanced99/notes/node192.html">ftp.sgi.com</a>
     * 
     * @param light
     *          the light's vector
     * @param a
     *          the x factor in the plane equation
     * @param b
     *          the y factor in the plane equation
     * @param c
     *          the z factor in the plane equation
     * @param d
     *          the constant in the plane equation
     * @return this
     */
    public Matrix4d shadow(Vector4d light, double a, double b, double c, double d) {
        return shadow(light.x, light.y, light.z, light.w, a, b, c, d, this);
    }

    /**
     * Apply a projection transformation to this matrix that projects onto the plane specified via the general plane equation
     * <tt>x*a + y*b + z*c + d = 0</tt> as if casting a shadow from a given light position/direction <code>light</code>
     * and store the result in <code>dest</code>.
     * <p>
     * If <tt>light.w</tt> is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the shadow matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * reflection will be applied first!
     * <p>
     * Reference: <a href="ftp://ftp.sgi.com/opengl/contrib/blythe/advanced99/notes/node192.html">ftp.sgi.com</a>
     * 
     * @param light
     *          the light's vector
     * @param a
     *          the x factor in the plane equation
     * @param b
     *          the y factor in the plane equation
     * @param c
     *          the z factor in the plane equation
     * @param d
     *          the constant in the plane equation
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d shadow(Vector4d light, double a, double b, double c, double d, Matrix4d dest) {
        return shadow(light.x, light.y, light.z, light.w, a, b, c, d, dest);
    }

    /**
     * Apply a projection transformation to this matrix that projects onto the plane specified via the general plane equation
     * <tt>x*a + y*b + z*c + d = 0</tt> as if casting a shadow from a given light position/direction <tt>(lightX, lightY, lightZ, lightW)</tt>.
     * <p>
     * If <code>lightW</code> is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the shadow matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * reflection will be applied first!
     * <p>
     * Reference: <a href="ftp://ftp.sgi.com/opengl/contrib/blythe/advanced99/notes/node192.html">ftp.sgi.com</a>
     * 
     * @param lightX
     *          the x-component of the light's vector
     * @param lightY
     *          the y-component of the light's vector
     * @param lightZ
     *          the z-component of the light's vector
     * @param lightW
     *          the w-component of the light's vector
     * @param a
     *          the x factor in the plane equation
     * @param b
     *          the y factor in the plane equation
     * @param c
     *          the z factor in the plane equation
     * @param d
     *          the constant in the plane equation
     * @return this
     */
    public Matrix4d shadow(double lightX, double lightY, double lightZ, double lightW, double a, double b, double c, double d) {
        return shadow(lightX, lightY, lightZ, lightW, a, b, c, d, this);
    }

    /**
     * Apply a projection transformation to this matrix that projects onto the plane specified via the general plane equation
     * <tt>x*a + y*b + z*c + d = 0</tt> as if casting a shadow from a given light position/direction <tt>(lightX, lightY, lightZ, lightW)</tt>
     * and store the result in <code>dest</code>.
     * <p>
     * If <code>lightW</code> is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the shadow matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * reflection will be applied first!
     * <p>
     * Reference: <a href="ftp://ftp.sgi.com/opengl/contrib/blythe/advanced99/notes/node192.html">ftp.sgi.com</a>
     * 
     * @param lightX
     *          the x-component of the light's vector
     * @param lightY
     *          the y-component of the light's vector
     * @param lightZ
     *          the z-component of the light's vector
     * @param lightW
     *          the w-component of the light's vector
     * @param a
     *          the x factor in the plane equation
     * @param b
     *          the y factor in the plane equation
     * @param c
     *          the z factor in the plane equation
     * @param d
     *          the constant in the plane equation
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d shadow(double lightX, double lightY, double lightZ, double lightW, double a, double b, double c, double d, Matrix4d dest) {
        // normalize plane
        double invPlaneLen = 1.0 / Math.sqrt(a*a + b*b + c*c);
        double an = a * invPlaneLen;
        double bn = b * invPlaneLen;
        double cn = c * invPlaneLen;
        double dn = d * invPlaneLen;

        double dot = an * lightX + bn * lightY + cn * lightZ + dn * lightW;

        // compute right matrix elements
        double rn00 = dot - an * lightX;
        double rn01 = -an * lightY;
        double rn02 = -an * lightZ;
        double rn03 = -an * lightW;
        double rn10 = -bn * lightX;
        double rn11 = dot - bn * lightY;
        double rn12 = -bn * lightZ;
        double rn13 = -bn * lightW;
        double rn20 = -cn * lightX;
        double rn21 = -cn * lightY;
        double rn22 = dot - cn * lightZ;
        double rn23 = -cn * lightW;
        double rn30 = -dn * lightX;
        double rn31 = -dn * lightY;
        double rn32 = -dn * lightZ;
        double rn33 = dot - dn * lightW;

        // matrix multiplication
        double nms00 = ms[M00] * rn00 + ms[M10] * rn01 + ms[M20] * rn02 + ms[M30] * rn03;
        double nms01 = ms[M01] * rn00 + ms[M11] * rn01 + ms[M21] * rn02 + ms[M31] * rn03;
        double nms02 = ms[M02] * rn00 + ms[M12] * rn01 + ms[M22] * rn02 + ms[M32] * rn03;
        double nms03 = ms[M03] * rn00 + ms[M13] * rn01 + ms[M23] * rn02 + ms[M33] * rn03;
        double nms10 = ms[M00] * rn10 + ms[M10] * rn11 + ms[M20] * rn12 + ms[M30] * rn13;
        double nms11 = ms[M01] * rn10 + ms[M11] * rn11 + ms[M21] * rn12 + ms[M31] * rn13;
        double nms12 = ms[M02] * rn10 + ms[M12] * rn11 + ms[M22] * rn12 + ms[M32] * rn13;
        double nms13 = ms[M03] * rn10 + ms[M13] * rn11 + ms[M23] * rn12 + ms[M33] * rn13;
        double nms20 = ms[M00] * rn20 + ms[M10] * rn21 + ms[M20] * rn22 + ms[M30] * rn23;
        double nms21 = ms[M01] * rn20 + ms[M11] * rn21 + ms[M21] * rn22 + ms[M31] * rn23;
        double nms22 = ms[M02] * rn20 + ms[M12] * rn21 + ms[M22] * rn22 + ms[M32] * rn23;
        double nms23 = ms[M03] * rn20 + ms[M13] * rn21 + ms[M23] * rn22 + ms[M33] * rn23;
        dest.ms[M30] = ms[M00] * rn30 + ms[M10] * rn31 + ms[M20] * rn32 + ms[M30] * rn33;
        dest.ms[M31] = ms[M01] * rn30 + ms[M11] * rn31 + ms[M21] * rn32 + ms[M31] * rn33;
        dest.ms[M32] = ms[M02] * rn30 + ms[M12] * rn31 + ms[M22] * rn32 + ms[M32] * rn33;
        dest.ms[M33] = ms[M03] * rn30 + ms[M13] * rn31 + ms[M23] * rn32 + ms[M33] * rn33;
        dest.ms[M00] = nms00;
        dest.ms[M01] = nms01;
        dest.ms[M02] = nms02;
        dest.ms[M03] = nms03;
        dest.ms[M10] = nms10;
        dest.ms[M11] = nms11;
        dest.ms[M12] = nms12;
        dest.ms[M13] = nms13;
        dest.ms[M20] = nms20;
        dest.ms[M21] = nms21;
        dest.ms[M22] = nms22;
        dest.ms[M23] = nms23;

        return dest;
    }

    /**
     * Apply a projection transformation to this matrix that projects onto the plane with the general plane equation
     * <tt>y = 0</tt> as if casting a shadow from a given light position/direction <code>light</code>
     * and store the result in <code>dest</code>.
     * <p>
     * Before the shadow projection is applied, the plane is transformed via the specified <code>planeTransformation</code>.
     * <p>
     * If <tt>light.w</tt> is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the shadow matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * reflection will be applied first!
     * 
     * @param light
     *          the light's vector
     * @param planeTransform
     *          the transformation to transform the implied plane <tt>y = 0</tt> before applying the projection
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d shadow(Vector4d light, Matrix4d planeTransform, Matrix4d dest) {
        // compute plane equation by transforming (y = 0)
        double a = planeTransform.ms[M10];
        double b = planeTransform.ms[M11];
        double c = planeTransform.ms[M12];
        double d = -a * planeTransform.ms[M30] - b * planeTransform.ms[M31] - c * planeTransform.ms[M32];
        return shadow(light.x, light.y, light.z, light.w, a, b, c, d, dest);
    }

    /**
     * Apply a projection transformation to this matrix that projects onto the plane with the general plane equation
     * <tt>y = 0</tt> as if casting a shadow from a given light position/direction <code>light</code>.
     * <p>
     * Before the shadow projection is applied, the plane is transformed via the specified <code>planeTransformation</code>.
     * <p>
     * If <tt>light.w</tt> is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the shadow matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * reflection will be applied first!
     * 
     * @param light
     *          the light's vector
     * @param planeTransform
     *          the transformation to transform the implied plane <tt>y = 0</tt> before applying the projection
     * @return this
     */
    public Matrix4d shadow(Vector4d light, Matrix4d planeTransform) {
        return shadow(light, planeTransform, this);
    }

    /**
     * Apply a projection transformation to this matrix that projects onto the plane with the general plane equation
     * <tt>y = 0</tt> as if casting a shadow from a given light position/direction <tt>(lightX, lightY, lightZ, lightW)</tt>
     * and store the result in <code>dest</code>.
     * <p>
     * Before the shadow projection is applied, the plane is transformed via the specified <code>planeTransformation</code>.
     * <p>
     * If <code>lightW</code> is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the shadow matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * reflection will be applied first!
     * 
     * @param lightX
     *          the x-component of the light vector
     * @param lightY
     *          the y-component of the light vector
     * @param lightZ
     *          the z-component of the light vector
     * @param lightW
     *          the w-component of the light vector
     * @param planeTransform
     *          the transformation to transform the implied plane <tt>y = 0</tt> before applying the projection
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d shadow(double lightX, double lightY, double lightZ, double lightW, Matrix4d planeTransform, Matrix4d dest) {
        // compute plane equation by transforming (y = 0)
        double a = planeTransform.ms[M10];
        double b = planeTransform.ms[M11];
        double c = planeTransform.ms[M12];
        double d = -a * planeTransform.ms[M30] - b * planeTransform.ms[M31] - c * planeTransform.ms[M32];
        return shadow(lightX, lightY, lightZ, lightW, a, b, c, d, dest);
    }

    /**
     * Apply a projection transformation to this matrix that projects onto the plane with the general plane equation
     * <tt>y = 0</tt> as if casting a shadow from a given light position/direction <tt>(lightX, lightY, lightZ, lightW)</tt>.
     * <p>
     * Before the shadow projection is applied, the plane is transformed via the specified <code>planeTransformation</code>.
     * <p>
     * If <code>lightW</code> is <tt>0.0</tt> the light is being treated as a directional light; if it is <tt>1.0</tt> it is a point light.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>S</code> the shadow matrix,
     * then the new matrix will be <code>M * S</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * S * v</code>, the
     * reflection will be applied first!
     * 
     * @param lightX
     *          the x-component of the light vector
     * @param lightY
     *          the y-component of the light vector
     * @param lightZ
     *          the z-component of the light vector
     * @param lightW
     *          the w-component of the light vector
     * @param planeTransform
     *          the transformation to transform the implied plane <tt>y = 0</tt> before applying the projection
     * @return this
     */
    public Matrix4d shadow(double lightX, double lightY, double lightZ, double lightW, Matrix4d planeTransform) {
        return shadow(lightX, lightY, lightZ, lightW, planeTransform, this);
    }

    /**
     * Set this matrix to a cylindrical billboard transformation that rotates the local +Z axis of a given object with position <code>objPos</code> towards
     * a target position at <code>targetPos</code> while constraining a cylindrical rotation around the given <code>up</code> vector.
     * <p>
     * This method can be used to create the complete model transformation for a given object, including the translation of the object to
     * its position <code>objPos</code>.
     * 
     * @param objPos
     *          the position of the object to rotate towards <code>targetPos</code>
     * @param targetPos
     *          the position of the target (for example the camera) towards which to rotate the object
     * @param up
     *          the rotation axis (must be {@link Vector3d#normalize() normalized})
     * @return this
     */
    public Matrix4d billboardCylindrical(Vector3d objPos, Vector3d targetPos, Vector3d up) {
        double dirX = targetPos.x - objPos.x;
        double dirY = targetPos.y - objPos.y;
        double dirZ = targetPos.z - objPos.z;
        // left = up x dir
        double leftX = up.y * dirZ - up.z * dirY;
        double leftY = up.z * dirX - up.x * dirZ;
        double leftZ = up.x * dirY - up.y * dirX;
        // normalize left
        double invLeftLen = 1.0 / Math.sqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        leftX *= invLeftLen;
        leftY *= invLeftLen;
        leftZ *= invLeftLen;
        // recompute dir by constraining rotation around 'up'
        // dir = left x up
        dirX = leftY * up.z - leftZ * up.y;
        dirY = leftZ * up.x - leftX * up.z;
        dirZ = leftX * up.y - leftY * up.x;
        // normalize dir
        double invDirLen = 1.0 / Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX *= invDirLen;
        dirY *= invDirLen;
        dirZ *= invDirLen;
        // set matrix elements
        ms[M00] = leftX;
        ms[M01] = leftY;
        ms[M02] = leftZ;
        ms[M03] = 0.0;
        ms[M10] = up.x;
        ms[M11] = up.y;
        ms[M12] = up.z;
        ms[M13] = 0.0;
        ms[M20] = dirX;
        ms[M21] = dirY;
        ms[M22] = dirZ;
        ms[M23] = 0.0;
        ms[M30] = objPos.x;
        ms[M31] = objPos.y;
        ms[M32] = objPos.z;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to a spherical billboard transformation that rotates the local +Z axis of a given object with position <code>objPos</code> towards
     * a target position at <code>targetPos</code>.
     * <p>
     * This method can be used to create the complete model transformation for a given object, including the translation of the object to
     * its position <code>objPos</code>.
     * <p>
     * If preserving an <i>up</i> vector is not necessary when rotating the +Z axis, then a shortest arc rotation can be obtained 
     * using {@link #billboardSpherical(Vector3d, Vector3d)}.
     * 
     * @see #billboardSpherical(Vector3d, Vector3d)
     * 
     * @param objPos
     *          the position of the object to rotate towards <code>targetPos</code>
     * @param targetPos
     *          the position of the target (for example the camera) towards which to rotate the object
     * @param up
     *          the up axis used to orient the object
     * @return this
     */
    public Matrix4d billboardSpherical(Vector3d objPos, Vector3d targetPos, Vector3d up) {
        double dirX = targetPos.x - objPos.x;
        double dirY = targetPos.y - objPos.y;
        double dirZ = targetPos.z - objPos.z;
        // normalize dir
        double invDirLen = 1.0 / Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX *= invDirLen;
        dirY *= invDirLen;
        dirZ *= invDirLen;
        // left = up x dir
        double leftX = up.y * dirZ - up.z * dirY;
        double leftY = up.z * dirX - up.x * dirZ;
        double leftZ = up.x * dirY - up.y * dirX;
        // normalize left
        double invLeftLen = 1.0 / Math.sqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        leftX *= invLeftLen;
        leftY *= invLeftLen;
        leftZ *= invLeftLen;
        // up = dir x left
        double upX = dirY * leftZ - dirZ * leftY;
        double upY = dirZ * leftX - dirX * leftZ;
        double upZ = dirX * leftY - dirY * leftX;
        // set matrix elements
        ms[M00] = leftX;
        ms[M01] = leftY;
        ms[M02] = leftZ;
        ms[M03] = 0.0;
        ms[M10] = upX;
        ms[M11] = upY;
        ms[M12] = upZ;
        ms[M13] = 0.0;
        ms[M20] = dirX;
        ms[M21] = dirY;
        ms[M22] = dirZ;
        ms[M23] = 0.0;
        ms[M30] = objPos.x;
        ms[M31] = objPos.y;
        ms[M32] = objPos.z;
        ms[M33] = 1.0;
        return this;
    }

    /**
     * Set this matrix to a spherical billboard transformation that rotates the local +Z axis of a given object with position <code>objPos</code> towards
     * a target position at <code>targetPos</code> using a shortest arc rotation by not preserving any <i>up</i> vector of the object.
     * <p>
     * This method can be used to create the complete model transformation for a given object, including the translation of the object to
     * its position <code>objPos</code>.
     * <p>
     * In order to specify an <i>up</i> vector which needs to be maintained when rotating the +Z axis of the object,
     * use {@link #billboardSpherical(Vector3d, Vector3d, Vector3d)}.
     * 
     * @see #billboardSpherical(Vector3d, Vector3d, Vector3d)
     * 
     * @param objPos
     *          the position of the object to rotate towards <code>targetPos</code>
     * @param targetPos
     *          the position of the target (for example the camera) towards which to rotate the object
     * @return this
     */
    public Matrix4d billboardSpherical(Vector3d objPos, Vector3d targetPos) {
        double toDirX = targetPos.x - objPos.x;
        double toDirY = targetPos.y - objPos.y;
        double toDirZ = targetPos.z - objPos.z;
        double x = -toDirY;
        double y = toDirX;
        double w = Math.sqrt(toDirX * toDirX + toDirY * toDirY + toDirZ * toDirZ) + toDirZ;
        double invNorm = 1.0 / Math.sqrt(x * x + y * y + w * w);
        x *= invNorm;
        y *= invNorm;
        w *= invNorm;
        double q00 = (x + x) * x;
        double q11 = (y + y) * y;
        double q01 = (x + x) * y;
        double q03 = (x + x) * w;
        double q13 = (y + y) * w;
        ms[M00] = 1.0 - q11;
        ms[M01] = q01;
        ms[M02] = -q13;
        ms[M03] = 0.0;
        ms[M10] = q01;
        ms[M11] = 1.0 - q00;
        ms[M12] = q03;
        ms[M13] = 0.0;
        ms[M20] = q13;
        ms[M21] = -q03;
        ms[M22] = 1.0 - q11 - q00;
        ms[M23] = 0.0;
        ms[M30] = objPos.x;
        ms[M31] = objPos.y;
        ms[M32] = objPos.z;
        ms[M33] = 1.0;
        return this;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(ms[M00]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M01]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M02]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M03]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M10]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M11]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M12]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M13]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M20]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M21]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M22]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M23]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M30]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M31]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M32]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ms[M33]);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Matrix4d))
            return false;
        Matrix4d other = (Matrix4d) obj;
        if (Double.doubleToLongBits(ms[M00]) != Double.doubleToLongBits(other.ms[M00]))
            return false;
        if (Double.doubleToLongBits(ms[M01]) != Double.doubleToLongBits(other.ms[M01]))
            return false;
        if (Double.doubleToLongBits(ms[M02]) != Double.doubleToLongBits(other.ms[M02]))
            return false;
        if (Double.doubleToLongBits(ms[M03]) != Double.doubleToLongBits(other.ms[M03]))
            return false;
        if (Double.doubleToLongBits(ms[M10]) != Double.doubleToLongBits(other.ms[M10]))
            return false;
        if (Double.doubleToLongBits(ms[M11]) != Double.doubleToLongBits(other.ms[M11]))
            return false;
        if (Double.doubleToLongBits(ms[M12]) != Double.doubleToLongBits(other.ms[M12]))
            return false;
        if (Double.doubleToLongBits(ms[M13]) != Double.doubleToLongBits(other.ms[M13]))
            return false;
        if (Double.doubleToLongBits(ms[M20]) != Double.doubleToLongBits(other.ms[M20]))
            return false;
        if (Double.doubleToLongBits(ms[M21]) != Double.doubleToLongBits(other.ms[M21]))
            return false;
        if (Double.doubleToLongBits(ms[M22]) != Double.doubleToLongBits(other.ms[M22]))
            return false;
        if (Double.doubleToLongBits(ms[M23]) != Double.doubleToLongBits(other.ms[M23]))
            return false;
        if (Double.doubleToLongBits(ms[M30]) != Double.doubleToLongBits(other.ms[M30]))
            return false;
        if (Double.doubleToLongBits(ms[M31]) != Double.doubleToLongBits(other.ms[M31]))
            return false;
        if (Double.doubleToLongBits(ms[M32]) != Double.doubleToLongBits(other.ms[M32]))
            return false;
        if (Double.doubleToLongBits(ms[M33]) != Double.doubleToLongBits(other.ms[M33]))
            return false;
        return true;
    }

    /**
     * Apply a picking transformation to this matrix using the given window coordinates <tt>(x, y)</tt> as the pick center
     * and the given <tt>(width, height)</tt> as the size of the picking region in window coordinates, and store the result
     * in <code>dest</code>.
     * 
     * @param x
     *          the x coordinate of the picking region center in window coordinates
     * @param y
     *          the y coordinate of the picking region center in window coordinates
     * @param width
     *          the width of the picking region in window coordinates
     * @param height
     *          the height of the picking region in window coordinates
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @param dest
     *          the destination matrix, which will hold the result
     * @return dest
     */
    public Matrix4d pick(double x, double y, double width, double height, int[] viewport, Matrix4d dest) {
        double sx = viewport[2] / width;
        double sy = viewport[3] / height;
        double tx = (viewport[2] + 2.0 * (viewport[0] - x)) / width;
        double ty = (viewport[3] + 2.0 * (viewport[1] - y)) / height;
        dest.ms[M30] = ms[M00] * tx + ms[M10] * ty + ms[M30];
        dest.ms[M31] = ms[M01] * tx + ms[M11] * ty + ms[M31];
        dest.ms[M32] = ms[M02] * tx + ms[M12] * ty + ms[M32];
        dest.ms[M33] = ms[M03] * tx + ms[M13] * ty + ms[M33];
        dest.ms[M00] = ms[M00] * sx;
        dest.ms[M01] = ms[M01] * sx;
        dest.ms[M02] = ms[M02] * sx;
        dest.ms[M03] = ms[M03] * sx;
        dest.ms[M10] = ms[M10] * sy;
        dest.ms[M11] = ms[M11] * sy;
        dest.ms[M12] = ms[M12] * sy;
        dest.ms[M13] = ms[M13] * sy;
        return dest;
    }

    /**
     * Apply a picking transformation to this matrix using the given window coordinates <tt>(x, y)</tt> as the pick center
     * and the given <tt>(width, height)</tt> as the size of the picking region in window coordinates.
     * 
     * @param x
     *          the x coordinate of the picking region center in window coordinates
     * @param y
     *          the y coordinate of the picking region center in window coordinates
     * @param width
     *          the width of the picking region in window coordinates
     * @param height
     *          the height of the picking region in window coordinates
     * @param viewport
     *          the viewport described by <tt>[x, y, width, height]</tt>
     * @return this
     */
    public Matrix4d pick(double x, double y, double width, double height, int[] viewport) {
        return pick(x, y, width, height, viewport, this);
    }

    /**
     * Determine whether this matrix describes an affine transformation. This is the case iff its last row is equal to <tt>(0, 0, 0, 1)</tt>.
     * 
     * @return <code>true</code> iff this matrix is affine; <code>false</code> otherwise
     */
    public boolean isAffine() {
        return ms[M03] == 0.0 && ms[M13] == 0.0 && ms[M23] == 0.0 && ms[M33] == 1.0;
    }

    /**
     * Exchange the values of <code>this</code> matrix with the given <code>other</code> matrix.
     * 
     * @param other
     *          the other matrix to exchange the values with
     * @return this
     */
    public Matrix4d swap(Matrix4d other) {
        double tmp;
        tmp = ms[M00]; ms[M00] = other.ms[M00]; other.ms[M00] = tmp;
        tmp = ms[M01]; ms[M01] = other.ms[M01]; other.ms[M01] = tmp;
        tmp = ms[M02]; ms[M02] = other.ms[M02]; other.ms[M02] = tmp;
        tmp = ms[M03]; ms[M03] = other.ms[M03]; other.ms[M03] = tmp;
        tmp = ms[M10]; ms[M10] = other.ms[M10]; other.ms[M10] = tmp;
        tmp = ms[M11]; ms[M11] = other.ms[M11]; other.ms[M11] = tmp;
        tmp = ms[M12]; ms[M12] = other.ms[M12]; other.ms[M12] = tmp;
        tmp = ms[M13]; ms[M13] = other.ms[M13]; other.ms[M13] = tmp;
        tmp = ms[M20]; ms[M20] = other.ms[M20]; other.ms[M20] = tmp;
        tmp = ms[M21]; ms[M21] = other.ms[M21]; other.ms[M21] = tmp;
        tmp = ms[M22]; ms[M22] = other.ms[M22]; other.ms[M22] = tmp;
        tmp = ms[M23]; ms[M23] = other.ms[M23]; other.ms[M23] = tmp;
        tmp = ms[M30]; ms[M30] = other.ms[M30]; other.ms[M30] = tmp;
        tmp = ms[M31]; ms[M31] = other.ms[M31]; other.ms[M31] = tmp;
        tmp = ms[M32]; ms[M32] = other.ms[M32]; other.ms[M32] = tmp;
        tmp = ms[M33]; ms[M33] = other.ms[M33]; other.ms[M33] = tmp;
        return this;
    }

    /**
     * Apply an arcball view transformation to this matrix with the given <code>radius</code> and <code>center</code>
     * position of the arcball and the specified X and Y rotation angles, and store the result in <code>dest</code>.
     * <p>
     * This method is equivalent to calling: <tt>translate(0, 0, -radius).rotateX(angleX).rotateY(angleY).translate(-center.x, -center.y, -center.z)</tt>
     * 
     * @param radius
     *          the arcball radius
     * @param center
     *          the center position of the arcball
     * @param angleX
     *          the rotation angle around the X axis in radians
     * @param angleY
     *          the rotation angle around the Y axis in radians
     * @param dest
     *          will hold the result
     * @return dest
     */
    public Matrix4d arcball(double radius, Vector3d center, double angleX, double angleY, Matrix4d dest) {
        return translate(0, 0, -radius, dest).rotateX(angleX).rotateY(angleY).translate(-center.x, -center.y, -center.z);
    }

    /**
     * Apply an arcball view transformation to this matrix with the given <code>radius</code> and <code>center</code>
     * position of the arcball and the specified X and Y rotation angles.
     * <p>
     * This method is equivalent to calling: <tt>translate(0, 0, -radius).rotateX(angleX).rotateY(angleY).translate(-center.x, -center.y, -center.z)</tt>
     * 
     * @param radius
     *          the arcball radius
     * @param center
     *          the center position of the arcball
     * @param angleX
     *          the rotation angle around the X axis in radians
     * @param angleY
     *          the rotation angle around the Y axis in radians
     * @return this
     */
    public Matrix4d arcball(double radius, Vector3d center, double angleX, double angleY) {
        return arcball(radius, center, angleX, angleY, this);
    }

    /**
     * Compute the axis-aligned bounding box of the frustum described by <code>this</code> matrix and store the minimum corner
     * coordinates in the given <code>min</code> and the maximum corner coordinates in the given <code>max</code> vector.
     * <p>
     * The matrix <code>this</code> is assumed to be the {@link #invert() inverse} of the origial view-projection matrix
     * for which to compute the axis-aligned bounding box in world-space.
     * <p>
     * The axis-aligned bounding box of the unit frustum is <tt>(-1, -1, -1)</tt>, <tt>(1, 1, 1)</tt>.
     * 
     * @param min
     *          will hold the minimum corner coordinates of the axis-aligned bounding box
     * @param max
     *          will hold the maximum corner coordinates of the axis-aligned bounding box
     * @return this
     */
    public Matrix4d frustumAabb(Vector3d min, Vector3d max) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (int t = 0; t < 8; t++) {
            double x = ((t & 1) << 1) - 1.0;
            double y = (((t >>> 1) & 1) << 1) - 1.0;
            double z = (((t >>> 2) & 1) << 1) - 1.0;
            double invW = 1.0 / (ms[M03] * x + ms[M13] * y + ms[M23] * z + ms[M33]);
            double nx = (ms[M00] * x + ms[M10] * y + ms[M20] * z + ms[M30]) * invW;
            double ny = (ms[M01] * x + ms[M11] * y + ms[M21] * z + ms[M31]) * invW;
            double nz = (ms[M02] * x + ms[M12] * y + ms[M22] * z + ms[M32]) * invW;
            minX = minX < nx ? minX : nx;
            minY = minY < ny ? minY : ny;
            minZ = minZ < nz ? minZ : nz;
            maxX = maxX > nx ? maxX : nx;
            maxY = maxY > ny ? maxY : ny;
            maxZ = maxZ > nz ? maxZ : nz;
        }
        min.x = minX;
        min.y = minY;
        min.z = minZ;
        max.x = maxX;
        max.y = maxY;
        max.z = maxZ;
        return this;
    }

    /**
     * Compute the <i>range matrix</i> for the Projected Grid transformation as described in chapter "2.4.2 Creating the range conversion matrix"
     * of the paper <a href="http://fileadmin.cs.lth.se/graphics/theses/projects/projgrid/projgrid-lq.pdf">Real-time water rendering - Introducing the projected grid concept</a>
     * based on the <i>inverse</i> of the view-projection matrix which is assumed to be <code>this</code>, and store that range matrix into <code>dest</code>.
     * <p>
     * If the projected grid will not be visible then this method returns <code>null</code>.
     * <p>
     * This method uses the <tt>y = 0</tt> plane for the projection.
     * 
     * @param projector
     *          the projector view-projection transformation
     * @param sLower
     *          the lower (smallest) Y-coordinate which any transformed vertex might have while still being visible on the projected grid
     * @param sUpper
     *          the upper (highest) Y-coordinate which any transformed vertex might have while still being visible on the projected grid
     * @param dest
     *          will hold the resulting range matrix
     * @return the computed range matrix; or <code>null</code> if the projected grid will not be visible
     */
    public Matrix4d projectedGridRange(Matrix4d projector, double sLower, double sUpper, Matrix4d dest) {
        // Compute intersection with frustum edges and plane
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        boolean intersection = false;
        for (int t = 0; t < 3 * 4; t++) {
            double c0X, c0Y, c0Z;
            double c1X, c1Y, c1Z;
            if (t < 4) {
                // all x edges
                c0X = -1; c1X = +1;
                c0Y = c1Y = ((t & 1) << 1) - 1.0;
                c0Z = c1Z = (((t >>> 1) & 1) << 1) - 1.0;
            } else if (t < 8) {
                // all y edges
                c0Y = -1; c1Y = +1;
                c0X = c1X = ((t & 1) << 1) - 1.0;
                c0Z = c1Z = (((t >>> 1) & 1) << 1) - 1.0;
            } else {
                // all z edges
                c0Z = -1; c1Z = +1;
                c0X = c1X = ((t & 1) << 1) - 1.0;
                c0Y = c1Y = (((t >>> 1) & 1) << 1) - 1.0;
            }
            // unproject corners
            double invW = 1.0 / (ms[M03] * c0X + ms[M13] * c0Y + ms[M23] * c0Z + ms[M33]);
            double p0x = (ms[M00] * c0X + ms[M10] * c0Y + ms[M20] * c0Z + ms[M30]) * invW;
            double p0y = (ms[M01] * c0X + ms[M11] * c0Y + ms[M21] * c0Z + ms[M31]) * invW;
            double p0z = (ms[M02] * c0X + ms[M12] * c0Y + ms[M22] * c0Z + ms[M32]) * invW;
            invW = 1.0 / (ms[M03] * c1X + ms[M13] * c1Y + ms[M23] * c1Z + ms[M33]);
            double p1x = (ms[M00] * c1X + ms[M10] * c1Y + ms[M20] * c1Z + ms[M30]) * invW;
            double p1y = (ms[M01] * c1X + ms[M11] * c1Y + ms[M21] * c1Z + ms[M31]) * invW;
            double p1z = (ms[M02] * c1X + ms[M12] * c1Y + ms[M22] * c1Z + ms[M32]) * invW;
            double dirX = p1x - p0x;
            double dirY = p1y - p0y;
            double dirZ = p1z - p0z;
            double invDenom = 1.0 / dirY;
            // test for intersection
            for (int s = 0; s < 2; s++) {
                double isectT = -(p0y + (s == 0 ? sLower : sUpper)) * invDenom;
                if (isectT >= 0.0 && isectT <= 1.0) {
                    intersection = true;
                    // project with projector matrix
                    double ix = p0x + isectT * dirX;
                    double iz = p0z + isectT * dirZ;
                    invW = 1.0 / (projector.ms[M03] * ix + projector.ms[M23] * iz + projector.ms[M33]);
                    double px = (projector.ms[M00] * ix + projector.ms[M20] * iz + projector.ms[M30]) * invW;
                    double py = (projector.ms[M01] * ix + projector.ms[M21] * iz + projector.ms[M31]) * invW;
                    minX = minX < px ? minX : px;
                    minY = minY < py ? minY : py;
                    maxX = maxX > px ? maxX : px;
                    maxY = maxY > py ? maxY : py;
                }
            }
        }
        if (!intersection)
            return null; // <- projected grid is not visible
        return dest.set(maxX - minX, 0, 0, 0, 0, maxY - minY, 0, 0, 0, 0, 1, 0, minX, minY, 0, 1);
    }

    /**
     * Change the near and far clip plane distances of <code>this</code> perspective frustum transformation matrix
     * and store the result in <code>dest</code>.
     * <p>
     * This method only works if <code>this</code> is a perspective projection frustum transformation, for example obtained
     * via {@link #perspective(double, double, double, double) perspective()} or {@link #frustum(double, double, double, double, double, double) frustum()}.
     * 
     * @see #perspective(double, double, double, double)
     * @see #frustum(double, double, double, double, double, double)
     * 
     * @param near
     *          the new near clip plane distance
     * @param far
     *          the new far clip plane distance
     * @param dest
     *          will hold the resulting matrix
     * @return dest
     */
    public Matrix4d perspectiveFrustumSlice(double near, double far, Matrix4d dest) {
        double invOldNear = (ms[M23] + ms[M22]) / ms[M32];
        double invNearFar = 1.0 / (near - far);
        dest.ms[M00] = ms[M00] * invOldNear * near;
        dest.ms[M01] = ms[M01];
        dest.ms[M02] = ms[M02];
        dest.ms[M03] = ms[M03];
        dest.ms[M10] = ms[M10];
        dest.ms[M11] = ms[M11] * invOldNear * near;
        dest.ms[M12] = ms[M12];
        dest.ms[M13] = ms[M13];
        dest.ms[M20] = ms[M20];
        dest.ms[M21] = ms[M21];
        dest.ms[M22] = (far + near) * invNearFar;
        dest.ms[M23] = ms[M23];
        dest.ms[M30] = ms[M30];
        dest.ms[M31] = ms[M31];
        dest.ms[M32] = (far + far) * near * invNearFar;
        dest.ms[M33] = ms[M33];
        return dest;
    }

    /**
     * Build an ortographic projection transformation that fits the view-projection transformation represented by <code>this</code>
     * into the given affine <code>view</code> transformation.
     * <p>
     * The transformation represented by <code>this</code> must be given as the {@link #invert() inverse} of a typical combined camera view-projection
     * transformation, whose projection can be either orthographic or perspective.
     * <p>
     * The <code>view</code> must be an {@link #isAffine() affine} transformation which in the application of Cascaded Shadow Maps is usually the light view transformation.
     * It be obtained via any affine transformation or for example via {@link #lookAt(double, double, double, double, double, double, double, double, double) lookAt()}.
     * <p>
     * Reference: <a href="http://developer.download.nvidia.com/SDK/10.5/opengl/screenshots/samples/cascaded_shadow_maps.html">OpenGL SDK - Cascaded Shadow Maps</a>
     * 
     * @param view
     *          the view transformation to build a corresponding orthographic projection to fit the frustum of <code>this</code>
     * @param dest
     *          will hold the crop projection transformation
     * @return dest
     */
    public Matrix4d orthoCrop(Matrix4d view, Matrix4d dest) {
        // determine min/max world z and min/max orthographically view-projected x/y
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        for (int t = 0; t < 8; t++) {
            double x = ((t & 1) << 1) - 1.0;
            double y = (((t >>> 1) & 1) << 1) - 1.0;
            double z = (((t >>> 2) & 1) << 1) - 1.0;
            double invW = 1.0 / (ms[M03] * x + ms[M13] * y + ms[M23] * z + ms[M33]);
            double wx = (ms[M00] * x + ms[M10] * y + ms[M20] * z + ms[M30]) * invW;
            double wy = (ms[M01] * x + ms[M11] * y + ms[M21] * z + ms[M31]) * invW;
            double wz = (ms[M02] * x + ms[M12] * y + ms[M22] * z + ms[M32]) * invW;
            invW = 1.0 / (view.ms[M03] * wx + view.ms[M13] * wy + view.ms[M23] * wz + view.ms[M33]);
            double vx = view.ms[M00] * wx + view.ms[M10] * wy + view.ms[M20] * wz + view.ms[M30];
            double vy = view.ms[M01] * wx + view.ms[M11] * wy + view.ms[M21] * wz + view.ms[M31];
            double vz = (view.ms[M02] * wx + view.ms[M12] * wy + view.ms[M22] * wz + view.ms[M32]) * invW;
            minX = minX < vx ? minX : vx;
            maxX = maxX > vx ? maxX : vx;
            minY = minY < vy ? minY : vy;
            maxY = maxY > vy ? maxY : vy;
            minZ = minZ < vz ? minZ : vz;
            maxZ = maxZ > vz ? maxZ : vz;
        }
        // build crop projection matrix to fit 'this' frustum into view
        return dest.setOrtho(minX, maxX, minY, maxY, -maxZ, -minZ);
    }

    /**
     * Set <code>this</code> matrix to a perspective transformation that maps the trapezoid spanned by the four corner coordinates
     * <code>(p0x, p0y)</code>, <code>(p1x, p1y)</code>, <code>(p2x, p2y)</code> and <code>(p3x, p3y)</code> to the unit square <tt>[(-1, -1)..(+1, +1)]</tt>.
     * <p>
     * The corner coordinates are given in counter-clockwise order starting from the <i>left</i> corner on the smaller parallel side of the trapezoid
     * seen when looking at the trapezoid oriented with its shorter parallel edge at the bottom and its longer parallel edge at the top.
     * <p>
     * Reference: <a href="https://kenai.com/downloads/wpbdc/Documents/tsm.pdf">Notes On Implementation Of Trapezoidal Shadow Maps</a>
     * 
     * @param p0x
     *          the x coordinate of the left corner at the shorter edge of the trapezoid
     * @param p0y
     *          the y coordinate of the left corner at the shorter edge of the trapezoid
     * @param p1x
     *          the x coordinate of the right corner at the shorter edge of the trapezoid
     * @param p1y
     *          the y coordinate of the right corner at the shorter edge of the trapezoid
     * @param p2x
     *          the x coordinate of the right corner at the longer edge of the trapezoid
     * @param p2y
     *          the y coordinate of the right corner at the longer edge of the trapezoid
     * @param p3x
     *          the x coordinate of the left corner at the longer edge of the trapezoid
     * @param p3y
     *          the y coordinate of the left corner at the longer edge of the trapezoid
     * @return this
     */
    public Matrix4d trapezoidCrop(double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double p3x, double p3y) {
        double aX = p1y - p0y, aY = p0x - p1x;
        double m00 = aY;
        double m10 = -aX;
        double m30 = aX * p0y - aY * p0x;
        double m01 = aX;
        double m11 = aY;
        double m31 = -(aX * p0x + aY * p0y);
        double c3x = m00 * p3x + m10 * p3y + m30;
        double c3y = m01 * p3x + m11 * p3y + m31;
        double s = -c3x / c3y;
        m00 += s * m01;
        m10 += s * m11;
        m30 += s * m31;
        double d1x = m00 * p1x + m10 * p1y + m30;
        double d2x = m00 * p2x + m10 * p2y + m30;
        double d = d1x * c3y / (d2x - d1x);
        m31 += d;
        double sx = 2.0 / d2x;
        double sy = 1.0 / (c3y + d);
        double u = (sy + sy) * d / (1.0 - sy * d);
        double m03 = m01 * sy;
        double m13 = m11 * sy;
        double m33 = m31 * sy;
        m01 = (u + 1.0) * m03;
        m11 = (u + 1.0) * m13;
        m31 = (u + 1.0) * m33 - u;
        m00 = sx * m00 - m03;
        m10 = sx * m10 - m13;
        m30 = sx * m30 - m33;
        return set(m00, m01, 0, m03,
                   m10, m11, 0, m13,
                     0,   0, 1,   0,
                   m30, m31, 0, m33);
    }

}
