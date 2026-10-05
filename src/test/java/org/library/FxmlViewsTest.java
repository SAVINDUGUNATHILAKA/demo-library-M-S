package org.library;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class FxmlViewsTest {

    @BeforeAll
    public static void initJfx() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            // JavaFX Platform already initialized
            latch.countDown();
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX Toolkit startup timed out");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/org/library/view/Login.fxml",
        "/org/library/view/MainLayout.fxml",
        "/org/library/view/DashboardHome.fxml",
        "/org/library/view/AddBook.fxml",
        "/org/library/view/AddMember.fxml",
        "/org/library/view/ManageMembers.fxml",
        "/org/library/view/IssueBook.fxml",
        "/org/library/view/ReturnBook.fxml",
        "/org/library/view/BorrowingHistory.fxml"
    })
    public void testFxmlLoadsCleanly(String fxmlPath) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> errorRef = new AtomicReference<>();
        AtomicReference<Parent> rootRef = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                URL resource = getClass().getResource(fxmlPath);
                assertNotNull(resource, "FXML resource not found: " + fxmlPath);
                FXMLLoader loader = new FXMLLoader(resource);
                Parent root = loader.load();
                rootRef.set(root);
                assertNotNull(loader.getController(), "Controller should be bound for " + fxmlPath);
            } catch (Throwable t) {
                errorRef.set(t);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timed out loading " + fxmlPath);
        if (errorRef.get() != null) {
            errorRef.get().printStackTrace();
            fail("Failed to load FXML: " + fxmlPath + " due to: " + errorRef.get().getMessage());
        }
        assertNotNull(rootRef.get(), "Root parent should not be null for " + fxmlPath);
    }
}
