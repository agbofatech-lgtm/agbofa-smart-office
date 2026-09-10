package com.agbofa.smartoffice.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal object AgbofaIcons {
    val Home: ImageVector by lazy {
        material("Home") {
            path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
                moveTo(10f, 20f); verticalLineTo(14f); horizontalLineTo(14f); verticalLineTo(20f)
                horizontalLineTo(19f); verticalLineTo(12f); horizontalLineTo(22f); lineTo(12f, 3f)
                lineTo(2f, 12f); horizontalLineTo(5f); verticalLineTo(20f); close()
            }
        }
    }
    val Journal: ImageVector by lazy {
        material("Journal") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(3f, 4f); horizontalLineTo(17f); curveTo(18.1f, 4f, 19f, 4.9f, 19f, 6f)
                verticalLineTo(20f); curveTo(19f, 21.1f, 18.1f, 22f, 17f, 22f); horizontalLineTo(3f)
                curveTo(1.9f, 22f, 1f, 21.1f, 1f, 20f); verticalLineTo(6f); curveTo(1f, 4.9f, 1.9f, 4f, 3f, 4f); close()
                moveTo(5f, 8f); verticalLineTo(10f); horizontalLineTo(15f); verticalLineTo(8f); close()
                moveTo(5f, 12f); verticalLineTo(14f); horizontalLineTo(15f); verticalLineTo(12f); close()
                moveTo(5f, 16f); verticalLineTo(18f); horizontalLineTo(12f); verticalLineTo(16f); close()
            }
        }
    }
    val Decision: ImageVector by lazy {
        material("Decision") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 2f); lineTo(4f, 5f); verticalLineTo(11f)
                curveTo(4f, 16.55f, 7.16f, 21.74f, 12f, 23f)
                curveTo(16.84f, 21.74f, 20f, 16.55f, 20f, 11f); verticalLineTo(5f); close()
                moveTo(11f, 16f); lineTo(7f, 12f); lineTo(8.41f, 10.59f)
                lineTo(11f, 13.17f); lineTo(15.59f, 8.58f); lineTo(17f, 10f); close()
            }
        }
    }
    val Search: ImageVector by lazy {
        material("Search") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(15.5f, 14f); horizontalLineTo(14.71f); lineTo(14.43f, 13.73f)
                curveTo(15.41f, 12.59f, 16f, 11.11f, 16f, 9.5f); curveTo(16f, 5.91f, 13.09f, 3f, 9.5f, 3f)
                curveTo(5.91f, 3f, 3f, 5.91f, 3f, 9.5f); curveTo(3f, 13.09f, 5.91f, 16f, 9.5f, 16f)
                curveTo(11.11f, 16f, 12.59f, 15.41f, 13.73f, 14.43f); lineTo(14f, 14.71f)
                verticalLineTo(15.5f); lineTo(19f, 20.49f); lineTo(20.49f, 19f); close()
                moveTo(9.5f, 14f); curveTo(7.01f, 14f, 5f, 11.99f, 5f, 9.5f)
                curveTo(5f, 7.01f, 7.01f, 5f, 9.5f, 5f); curveTo(11.99f, 5f, 14f, 7.01f, 14f, 9.5f)
                curveTo(14f, 11.99f, 11.99f, 14f, 9.5f, 14f); close()
            }
        }
    }
    val Analytics: ImageVector by lazy {
        material("Analytics") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(5f, 9.2f); horizontalLineTo(8f); verticalLineTo(19f); horizontalLineTo(5f); close()
                moveTo(10.6f, 5f); horizontalLineTo(13.4f); verticalLineTo(19f); horizontalLineTo(10.6f); close()
                moveTo(16.2f, 13f); horizontalLineTo(19f); verticalLineTo(19f); horizontalLineTo(16.2f); close()
            }
        }
    }
    val Intelligence: ImageVector by lazy {
        material("Intelligence") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(9f, 21f); curveTo(9f, 21.55f, 9.45f, 22f, 10f, 22f); horizontalLineTo(14f)
                curveTo(14.55f, 22f, 15f, 21.55f, 15f, 21f); verticalLineTo(20f); horizontalLineTo(9f); close()
                moveTo(12f, 2f); curveTo(8.14f, 2f, 5f, 5.14f, 5f, 9f)
                curveTo(5f, 11.38f, 6.19f, 13.47f, 8f, 14.74f); verticalLineTo(17f)
                curveTo(8f, 17.55f, 8.45f, 18f, 9f, 18f); horizontalLineTo(15f)
                curveTo(15.55f, 18f, 16f, 17.55f, 16f, 17f); verticalLineTo(14.74f)
                curveTo(17.81f, 13.47f, 19f, 11.38f, 19f, 9f)
                curveTo(19f, 5.14f, 15.86f, 2f, 12f, 2f); close()
            }
        }
    }
}

private fun material(name: String, builder: ImageVector.Builder.() -> Unit): ImageVector =
    ImageVector.Builder(name = name, defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply(builder).build()
