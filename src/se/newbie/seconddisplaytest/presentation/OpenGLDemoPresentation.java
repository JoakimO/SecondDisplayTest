package se.newbie.seconddisplaytest.presentation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import se.newbie.seconddisplaytest.presentation.PresentationState.hasPresentationState;
import android.app.Presentation;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.view.Display;

public class OpenGLDemoPresentation extends Presentation implements hasPresentationState {

	PresentationState mState;
	OpenGLRenderer mOpenGLRenderer;

	public OpenGLDemoPresentation(Context aOuterContext, Display aDisplay, PresentationState aState) {
		super(aOuterContext, aDisplay);
		mState = aState;
		GLSurfaceView view = new GLSurfaceView(aOuterContext);
		mOpenGLRenderer = new OpenGLRenderer();
		view.setRenderer(mOpenGLRenderer);
		setContentView(view);
	}

	@Override
	public PresentationState getPresentationState() {
		mState.put("cube_rotation", Float.toString(mOpenGLRenderer.mCubeRotation));
		return mState;
	}

	class OpenGLRenderer implements Renderer {

		private Cube mCube = new Cube();
		private float mCubeRotation;

		public OpenGLRenderer() {
			String s = mState.get("cube_rotation");
			if (s != null) {
				mCubeRotation = Float.parseFloat(s);
			}
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
			gl.glClearDepthf(1.0f);
			gl.glEnable(GL10.GL_DEPTH_TEST);
			gl.glDepthFunc(GL10.GL_LEQUAL);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glLoadIdentity();
			gl.glTranslatef(0.0f, 0.0f, -10.0f);
			gl.glRotatef(mCubeRotation, 1.0f, 1.0f, 1.0f);
			mCube.draw(gl);
			gl.glLoadIdentity();
			mCubeRotation -= 0.15f;
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
		}

	}

	class Cube {

		private FloatBuffer mVertexBuffer;
		private FloatBuffer mColorBuffer;
		private ByteBuffer mIndexBuffer;

		private float vertices[] = { -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f };
		private float colors[] = { 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.5f, 0.0f, 1.0f, 1.0f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f };
		private byte indices[] = { 0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7, 3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6, 5, 3, 0, 1, 3, 1, 2 };

		public Cube() {
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			mVertexBuffer = byteBuf.asFloatBuffer();
			mVertexBuffer.put(vertices);
			mVertexBuffer.position(0);

			byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			mColorBuffer = byteBuf.asFloatBuffer();
			mColorBuffer.put(colors);
			mColorBuffer.position(0);

			mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
			mIndexBuffer.put(indices);
			mIndexBuffer.position(0);
		}

		public void draw(GL10 gl) {
			gl.glFrontFace(GL10.GL_CW);

			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

			gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}
}
