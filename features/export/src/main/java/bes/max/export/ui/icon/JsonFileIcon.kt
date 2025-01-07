package bes.max.export.ui.icon


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val jsonFileIcon: ImageVector
    get() {
        if (_jsonFileIcon != null) {
            return _jsonFileIcon!!
        }
        _jsonFileIcon = ImageVector.Builder(
            name = "File_json_icon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF5F6368)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(190f, 600f)
                horizontalLineToRelative(70f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(300f, 560f)
                verticalLineToRelative(-200f)
                horizontalLineToRelative(-60f)
                verticalLineToRelative(190f)
                horizontalLineToRelative(-40f)
                verticalLineToRelative(-50f)
                horizontalLineToRelative(-50f)
                verticalLineToRelative(60f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(190f, 600f)
                close()
                moveToRelative(177f, 0f)
                horizontalLineToRelative(60f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(467f, 560f)
                verticalLineToRelative(-60f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(427f, 460f)
                horizontalLineToRelative(-50f)
                verticalLineToRelative(-50f)
                horizontalLineToRelative(40f)
                verticalLineToRelative(20f)
                horizontalLineToRelative(50f)
                verticalLineToRelative(-30f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(427f, 360f)
                horizontalLineToRelative(-60f)
                quadToRelative(-17f, 0f, -28.5f, 11.5f)
                reflectiveQuadTo(327f, 400f)
                verticalLineToRelative(60f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(367f, 500f)
                horizontalLineToRelative(50f)
                verticalLineToRelative(50f)
                horizontalLineToRelative(-40f)
                verticalLineToRelative(-20f)
                horizontalLineToRelative(-50f)
                verticalLineToRelative(30f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(367f, 600f)
                close()
                moveToRelative(176f, -60f)
                verticalLineToRelative(-120f)
                horizontalLineToRelative(40f)
                verticalLineToRelative(120f)
                horizontalLineToRelative(-40f)
                close()
                moveToRelative(-10f, 60f)
                horizontalLineToRelative(60f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(633f, 560f)
                verticalLineToRelative(-160f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(593f, 360f)
                horizontalLineToRelative(-60f)
                quadToRelative(-17f, 0f, -28.5f, 11.5f)
                reflectiveQuadTo(493f, 400f)
                verticalLineToRelative(160f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(533f, 600f)
                close()
                moveToRelative(127f, 0f)
                horizontalLineToRelative(50f)
                verticalLineToRelative(-105f)
                lineToRelative(40f, 105f)
                horizontalLineToRelative(50f)
                verticalLineToRelative(-240f)
                horizontalLineToRelative(-50f)
                verticalLineToRelative(105f)
                lineToRelative(-40f, -105f)
                horizontalLineToRelative(-50f)
                verticalLineToRelative(240f)
                close()
                moveTo(120f, 800f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(40f, 720f)
                verticalLineToRelative(-480f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(120f, 160f)
                horizontalLineToRelative(720f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(920f, 240f)
                verticalLineToRelative(480f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(840f, 800f)
                horizontalLineTo(120f)
                close()
                moveToRelative(0f, -80f)
                horizontalLineToRelative(720f)
                verticalLineToRelative(-480f)
                horizontalLineTo(120f)
                verticalLineToRelative(480f)
                close()
                moveToRelative(0f, 0f)
                verticalLineToRelative(-480f)
                verticalLineToRelative(480f)
                close()
            }
        }.build()
        return _jsonFileIcon!!
    }

private var _jsonFileIcon: ImageVector? = null
