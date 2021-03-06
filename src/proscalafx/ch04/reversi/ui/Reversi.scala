package proscalafx.ch04.reversi.ui

import proscalafx.ch04.reversi.model.{Owner, WHITE, BLACK, ReversiModel}
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{ConditionalFeature, Platform, JFXApp}
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.effect.{DropShadow, InnerShadow}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Ellipse
import scalafx.scene.text.{FontWeight, Font, Text}
import scalafx.scene.transform.{Rotate, Translate, Scale}
import scalafx.scene.{PerspectiveCamera, Scene}


object Reversi extends JFXApp {

  val restart = new Button() {
    text = "Restart"
    onAction = handle {ReversiModel.restart()}
  }

  val game = new BorderPane() {
    top = createTitle()
    center = new StackPane() {
      content = List(
        createBackground(),
        createTiles()
      )
    }
    bottom = createScoreBoxes()
  }

  stage = new PrimaryStage() {
    scene = new Scene(600, 400) {
      root = new AnchorPane() {
        content = List(
          game,
          restart
        )
      }
    }
  }

  AnchorPane.setTopAnchor(game, 0d)
  AnchorPane.setBottomAnchor(game, 0d)
  AnchorPane.setLeftAnchor(game, 0d)
  AnchorPane.setRightAnchor(game, 0d)
  AnchorPane.setRightAnchor(restart, 10d)
  AnchorPane.setTopAnchor(restart, 10d)


  if (Platform.isSupported(ConditionalFeature.SCENE3D)) {
    stage.scene().camera = new PerspectiveCamera() {
      fieldOfView = 60
    }.delegate
  }


  //---------------------------------------------------------------------------


  private def createTitle() = new TilePane {
    snapToPixel = false
    content = List(
      new StackPane {
        style = "-fx-background-color: black"
        content = new Text {
          text = "ScalaFX"
          font = Font.font(null, FontWeight.BOLD, 18)
          fill = Color.WHITE
          alignmentInParent = Pos.CENTER_RIGHT
        }
      },
      new Text {
        text = "Reversi"
        font = Font.font(null, FontWeight.BOLD, 18)
        alignmentInParent = Pos.CENTER_LEFT
      })
    prefTileHeight = 40
    prefTileWidth <== parent.selectDouble("width") / 2
  }


  private def createBackground() = new Region {
    style = "-fx-background-color: radial-gradient(radius 100%, white, gray)"
  }


  private def createTiles(): GridPane = {
    val board = new GridPane
    for {
      i <- 0 until ReversiModel.BOARD_SIZE
      j <- 0 until ReversiModel.BOARD_SIZE
    } {
      val square = new ReversiSquare(i, j)
      val piece = new ReversiPiece()
      piece.owner <== ReversiModel.board(i)(j)
      board.add(new StackPane {
        content = List(square, piece)
      }, i, j)
    }

    if (Platform.isSupported(ConditionalFeature.SCENE3D)) {
      val scale = new Scale(.45, .8, 1, 300, 60, 0)
      val translate = new Translate(75, -2, -150)
      val xRot = new Rotate {
        angle = -40
        pivotX = 300
        pivotY = 150
        pivotZ = 0
        axis = Rotate.XAxis
      }
      val yRot = new Rotate {
        angle = -5
        pivotX = 300
        pivotY = 150
        pivotZ = 0
        axis = Rotate.YAxis
      }
      val zRot = new Rotate {
        angle = -6
        pivotX = 300
        pivotY = 150
        pivotZ = 0
        axis = Rotate.ZAxis
      }

      board.transforms ++= List(scale, translate, xRot, yRot, zRot)
    }

    board
  }


  private def createScoreBoxes() = new TilePane() {
    snapToPixel = false
    prefColumns = 2
    content = List(
      createScore(BLACK),
      createScore(WHITE)
    )
    prefTileWidth <== parent.selectDouble("width") / 2
  }


  private def createScore(owner: Owner): StackPane = {

    val innerShadow = new InnerShadow() {
      color = Color.DODGERBLUE
      choke = 0.5
    }
    val noInnerShadow = null.asInstanceOf[javafx.scene.effect.InnerShadow]

    val background = new Region() {
      style = "-fx-background-color: " + owner.opposite.colorStyle
      effect <== when(ReversiModel.turn === owner) choose innerShadow otherwise noInnerShadow
    }

    val dropShadow = new DropShadow() {
      color = Color.DODGERBLUE
      spread = 0.2
    }
    val noDropShadow = null.asInstanceOf[javafx.scene.effect.DropShadow]

    val piece = new Ellipse() {
      radiusX = 32
      radiusY = 20
      fill = owner.color
      effect <== when(ReversiModel.turn === owner) choose dropShadow otherwise noDropShadow
    }

    val score = new Text() {
      font = Font.font(null, FontWeight.BOLD, 100)
      fill = owner.color
      text <== ReversiModel.score(owner).asString()
    }

    val remaining = new Text() {
      font = Font.font(null, FontWeight.BOLD, 12)
      fill = owner.color
      text <== ReversiModel.turnsRemaining(owner).asString() + " turns remaining"
    }

    new StackPane() {
      content = List(
        background,
        new FlowPane() {
          hgap = 20
          vgap = 10
          alignment = Pos.CENTER
          content = List(
            score,
            new VBox() {
              alignment = Pos.CENTER
              spacing = 10
              content = List(
                piece,
                remaining
              )
            }
          )
        }
      )
    }
  }
}