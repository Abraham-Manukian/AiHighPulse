package com.vtempe.core.designsystem.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AiIcons {
    val Dashboard: ImageVector by lazy { dashboard() }
    val Strength: ImageVector by lazy { strength() }
    val Nutrition: ImageVector by lazy { nutrition() }
    val Sleep: ImageVector by lazy { sleep() }
    val Progress: ImageVector by lazy { progress() }
    val Chat: ImageVector by lazy { chat() }
    val Crown: ImageVector by lazy { crown() }
    val Settings: ImageVector by lazy { settings() }
    val Shopping: ImageVector by lazy { shopping() }

    private fun dashboard(): ImageVector = Builder(
        name = "AiDashboard",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        val stroke = SolidColor(Color(0xFF000000))
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(4f, 5.5f)
            lineTo(10f, 5.5f)
            lineTo(10f, 11.2f)
            lineTo(4f, 11.2f)
            close()
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(13.5f, 5f)
            lineTo(20f, 5f)
            lineTo(20f, 9.5f)
            lineTo(13.5f, 9.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(4f, 13f)
            lineTo(12.5f, 13f)
            lineTo(12.5f, 19.5f)
            lineTo(4f, 19.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(14.5f, 12.2f)
            lineTo(20f, 12.2f)
            lineTo(20f, 20f)
            lineTo(14.5f, 20f)
            close()
        }
    }.build()

    private fun strength(): ImageVector = Builder(
        name = "AiStrength",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        val stroke = SolidColor(Color(0xFF000000))
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(5f, 9.5f)
            lineTo(5f, 14.5f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(7.5f, 7f)
            lineTo(7.5f, 17f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(16.5f, 7f)
            lineTo(16.5f, 17f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(19f, 9.5f)
            lineTo(19f, 14.5f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.9f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(7.5f, 12f)
            lineTo(16.5f, 12f)
        }
    }.build()

    private fun nutrition(): ImageVector = Builder(
        name = "AiNutrition",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        val stroke = SolidColor(Color(0xFF000000))
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 5.5f)
            curveTo(15.6f, 3.6f, 20f, 5.8f, 20f, 10.2f)
            curveTo(20f, 15.8f, 15.5f, 19.3f, 12f, 20.8f)
            curveTo(8.5f, 19.3f, 4f, 15.8f, 4f, 10.2f)
            curveTo(4f, 5.8f, 8.4f, 3.6f, 12f, 5.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12.2f, 4.8f)
            curveTo(11.8f, 3.9f, 11f, 3.1f, 9.7f, 2.7f)
            curveTo(10.6f, 2.1f, 12f, 1.8f, 13.4f, 2.2f)
            curveTo(14.6f, 2.6f, 15.5f, 3.4f, 16f, 4.5f)
        }
    }.build()

    private fun sleep(): ImageVector = Builder(
        name = "AiSleep",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        val stroke = SolidColor(Color(0xFF000000))
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(16.7f, 5.3f)
            curveTo(13.9f, 6f, 12.1f, 8.4f, 12.1f, 11.2f)
            curveTo(12.1f, 14f, 13.9f, 16.5f, 16.7f, 17.2f)
            curveTo(15.1f, 18.5f, 12.9f, 19.1f, 10.8f, 18.7f)
            curveTo(7.3f, 18.1f, 4.8f, 15.1f, 4.8f, 11.6f)
            curveTo(4.8f, 8.1f, 7.3f, 5.1f, 10.8f, 4.5f)
            curveTo(12.9f, 4.1f, 15.1f, 4.7f, 16.7f, 5.3f)
            close()
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(18.2f, 4f)
            lineTo(20.5f, 4f)
            lineTo(18.6f, 7f)
            lineTo(20.9f, 7f)
        }
    }.build()

    private fun progress(): ImageVector = Builder(
        name = "AiProgress",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        val stroke = SolidColor(Color(0xFF000000))
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(4.5f, 16.5f)
            lineTo(9.2f, 11.8f)
            lineTo(12.4f, 14.6f)
            lineTo(17.8f, 8.5f)
            lineTo(20f, 10.7f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(4.5f, 9.2f)
            lineTo(4.5f, 16.5f)
            horizontalLineTo(11.8f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(15.3f, 7.4f)
            lineTo(20f, 7.4f)
            lineTo(20f, 12.1f)
        }
    }.build()

    private fun chat(): ImageVector = Builder(
        name = "AiChat",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        val stroke = SolidColor(Color(0xFF000000))
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(6.5f, 5.5f)
            horizontalLineTo(17.5f)
            curveTo(19.4f, 5.5f, 21f, 7f, 21f, 8.9f)
            verticalLineTo(12.3f)
            curveTo(21f, 14.2f, 19.4f, 15.7f, 17.5f, 15.7f)
            horizontalLineTo(12.8f)
            lineTo(9f, 19f)
            verticalLineTo(15.7f)
            horizontalLineTo(6.5f)
            curveTo(4.6f, 15.7f, 3f, 14.2f, 3f, 12.3f)
            verticalLineTo(8.9f)
            curveTo(3f, 7f, 4.6f, 5.5f, 6.5f, 5.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(8f, 10.6f)
            lineTo(16f, 10.6f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(8f, 12.8f)
            lineTo(13.5f, 12.8f)
        }
    }.build()

    private fun crown(): ImageVector = Builder(
        name = "AiCrown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        val stroke = SolidColor(Color(0xFF000000))
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(4f, 8.5f)
            lineTo(7.5f, 13f)
            lineTo(12f, 7f)
            lineTo(16.5f, 13f)
            lineTo(20f, 8.5f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(5.8f, 16.5f)
            horizontalLineTo(18.2f)
            arcToRelative(1.8f, 1.8f, 0f, false, true, 1.8f, 1.8f)
            verticalLineTo(18.6f)
            arcToRelative(1.8f, 1.8f, 0f, false, true, -1.8f, 1.8f)
            horizontalLineTo(5.8f)
            arcToRelative(1.8f, 1.8f, 0f, false, true, -1.8f, -1.8f)
            verticalLineTo(18.3f)
            arcToRelative(1.8f, 1.8f, 0f, false, true, 1.8f, -1.8f)
            close()
        }
    }.build()

    private fun settings(): ImageVector = Builder(
        name = "AiSettings",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        val stroke = SolidColor(Color(0xFF000000))
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 8.2f)
            arcToRelative(3.8f, 3.8f, 0f, true, true, 0f, 7.6f)
            arcToRelative(3.8f, 3.8f, 0f, true, true, 0f, -7.6f)
            close()
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 3.5f)
            lineTo(12f, 5.4f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(18.3f, 5.7f)
            lineTo(17f, 7f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(20.5f, 12f)
            lineTo(18.6f, 12f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(18.3f, 18.3f)
            lineTo(17f, 17f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 20.5f)
            lineTo(12f, 18.6f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(5.7f, 18.3f)
            lineTo(7f, 17f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3.5f, 12f)
            lineTo(5.4f, 12f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(5.7f, 5.7f)
            lineTo(7f, 7f)
        }
    }.build()

    private fun shopping(): ImageVector = Builder(
        name = "AiShopping",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        val stroke = SolidColor(Color(0xFF000000))
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(6.5f, 7f)
            lineTo(17.5f, 7f)
            lineTo(19.3f, 9.2f)
            verticalLineTo(18.8f)
            arcToRelative(1.8f, 1.8f, 0f, false, true, -1.8f, 1.8f)
            horizontalLineTo(6.5f)
            arcToRelative(1.8f, 1.8f, 0f, false, true, -1.8f, -1.8f)
            verticalLineTo(9.2f)
            close()
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(8.5f, 7f)
            verticalLineTo(5.8f)
            curveTo(8.5f, 4.2f, 9.8f, 2.8f, 12.5f, 2.8f)
            curveTo(15.2f, 2.8f, 16.5f, 4.2f, 16.5f, 5.8f)
            verticalLineTo(7f)
        }
        path(
            fill = SolidColor(Color(0x00000000)),
            stroke = stroke,
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Miter
        ) {
            moveTo(10f, 11.5f)
            lineTo(14f, 11.5f)
        }
    }.build()
}

