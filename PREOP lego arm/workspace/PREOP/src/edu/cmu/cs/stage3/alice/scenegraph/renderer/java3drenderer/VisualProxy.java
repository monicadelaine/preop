/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer;

class VisualProxy extends ComponentProxy {
    private GeometryProxy m_geometry = null;
    private boolean m_isShowing = false;
    private javax.media.j3d.TransformGroup m_j3dScaleGroup = new javax.media.j3d.TransformGroup();
    private javax.media.j3d.Transform3D m_j3dScale3D = new javax.media.j3d.Transform3D();
    private javax.media.j3d.Shape3D m_j3dShape3D = new javax.media.j3d.Shape3D();

    protected javax.media.j3d.Node getJ3DNode() {
        return m_j3dScaleGroup;
    }
    protected void initJ3D() {
        super.initJ3D();

		m_j3dScaleGroup.setCapability( javax.media.j3d.TransformGroup.ALLOW_TRANSFORM_WRITE );
		m_j3dScaleGroup.setPickable( true );
        m_j3dScaleGroup.setUserData( this );

        m_j3dScale3D.setIdentity();
        m_j3dScaleGroup.setTransform( m_j3dScale3D );

        m_j3dShape3D.setCapability( javax.media.j3d.Shape3D.ALLOW_APPEARANCE_WRITE );
        m_j3dShape3D.setCapability( javax.media.j3d.Shape3D.ALLOW_GEOMETRY_WRITE );
		m_j3dShape3D.setCapability( javax.media.j3d.Shape3D.ALLOW_GEOMETRY_READ );
        m_j3dShape3D.setPickable( true );
        m_j3dShape3D.setUserData( this );

        //todo?
        m_j3dScaleGroup.addChild( m_j3dShape3D );
    }
    public void updateJ3DGeometry() {
        javax.media.j3d.Geometry j3dGeometry;
        if( m_geometry != null ) {
            if( m_isShowing ) {
                j3dGeometry = m_geometry.getJ3DGeometry();
            } else {
                j3dGeometry = null;
            }
        } else {
            j3dGeometry = null;
        }
        m_j3dShape3D.setGeometry( j3dGeometry );
    }
	protected void changed( edu.cmu.cs.stage3.alice.scenegraph.Property property, Object value ) {
		if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.FRONT_FACING_APPEARANCE_PROPERTY ) {
            AppearanceProxy appearanceProxy = (AppearanceProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.Appearance)value );
            m_j3dShape3D.setAppearance( appearanceProxy.getJ3DAppearance() );
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.BACK_FACING_APPEARANCE_PROPERTY ) {
            //todo
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.GEOMETRY_PROPERTY ) {
            if( m_geometry != null ) {
                m_geometry.removeVisual( this );
            }
            m_geometry = (GeometryProxy)getProxyFor( (edu.cmu.cs.stage3.alice.scenegraph.Geometry)value );
            if( m_geometry != null ) {
                m_geometry.addVisual( this );
            }
            updateJ3DGeometry();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.SCALE_PROPERTY ) {
            javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
            m.set( (javax.vecmath.Matrix3d)value );

            //adjust from rows to columns
            m.transpose();

            //switch from left-handed to right-handed
                                            m.m02 = -m.m02;
                                            m.m12 = -m.m12;
            m.m20 = -m.m20; m.m21 = -m.m21;

            m_j3dScale3D.set( m );
            m_j3dScaleGroup.setTransform( m_j3dScale3D );

		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.IS_SHOWING_PROPERTY ) {
            m_isShowing = value!=null && ((Boolean)value).booleanValue();
            updateJ3DGeometry();
		} else if( property == edu.cmu.cs.stage3.alice.scenegraph.Visual.DISABLED_AFFECTORS_PROPERTY ) {
            //todo
		} else {
			super.changed( property, value );
		}
	}
}