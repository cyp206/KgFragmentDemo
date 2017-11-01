uniform mat4 uMVPMatrix;
attribute vec4 position;
attribute vec4 inputTextureCoordinate;

varying vec2 textureCoordinate;

void main()
{
    gl_Position = position   ;
//	textureCoordinate = (uMVPMatrix * inputTextureCoordinate).xy;
    textureCoordinate = inputTextureCoordinate.xy ;
}