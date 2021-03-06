package com.insightfullogic.honest_profiler.delivery.javafx.profile;

import com.insightfullogic.honest_profiler.core.collector.ProfileNode;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;

import static com.insightfullogic.honest_profiler.delivery.javafx.Rendering.renderMethod;
import static com.insightfullogic.honest_profiler.delivery.javafx.Rendering.renderTimeShare;
import static com.insightfullogic.honest_profiler.delivery.javafx.profile.TreeViewModel.MethodNodeAdapter;
import static com.insightfullogic.honest_profiler.delivery.javafx.profile.TreeViewModel.ThreadNodeAdapter;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.WHEAT;

public class TreeViewCell extends TreeCell<ProfileNode> {

    private static final int IMAGE_WIDTH = 50;
    private static final int IMAGE_HEIGHT = 15;

    private static final int TEXT_HORIZONTAL_INSET = 10;
    private static final int TEXT_VERTICAL_INSET = 12;
    /**
     * Not threadsafe: must be run on JavaFx thread.
     */
    @Override
    protected void updateItem(ProfileNode profileNode, boolean empty) {
        super.updateItem(profileNode, empty);

        TreeItem<ProfileNode> treeItem = getTreeItem();

        if (treeItem instanceof ThreadNodeAdapter) {
            ThreadNodeAdapter adapter = (ThreadNodeAdapter) treeItem;
            setText("Thread " + adapter.getThreadId());
            setGraphic(null);
        } else if (treeItem instanceof MethodNodeAdapter) {
            renderMethodNode(profileNode, empty);
        }
    }

    private void renderMethodNode(ProfileNode profileNode, boolean empty) {
        if (!empty && isVisible()) {
            setText(renderMethod(profileNode.getMethod()));
            Canvas canvas = new Canvas(IMAGE_WIDTH, IMAGE_HEIGHT);
            GraphicsContext context = canvas.getGraphicsContext2D();
            context.setFill(Color.BLACK);
            context.strokeRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

            double timeShare = profileNode.getTotalTimeShare();
            double scaledShare = timeShare * IMAGE_WIDTH;
            double xStart = IMAGE_WIDTH - scaledShare;
            context.setFill(Color.GREEN);
            context.fillRect(xStart, 0, scaledShare, IMAGE_HEIGHT);

            Color color = timeShare > 0.5 ? WHEAT : RED;
            context.setFill(color);
            context.fillText(renderTimeShare(timeShare), TEXT_HORIZONTAL_INSET, TEXT_VERTICAL_INSET);

            setGraphic(canvas);
        }
    }

}
