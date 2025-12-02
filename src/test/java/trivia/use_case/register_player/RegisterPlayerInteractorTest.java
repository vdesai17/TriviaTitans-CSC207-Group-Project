package trivia.use_case.register_player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import trivia.entity.Player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class RegisterPlayerInteractorTest {

    private RegisterPlayerDataAccessInterface playerDAO;
    private RegisterPlayerInteractor interactor;

    @BeforeEach
    void setUp() {
        playerDAO = mock(RegisterPlayerDataAccessInterface.class);
        interactor = new RegisterPlayerInteractor(playerDAO);
    }

    @Test
    void testRegisterPlayerSuccess() {
        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);

        Player result = interactor.registerPlayer("Vivan");

        // Check returned player
        assertNotNull(result);
        assertEquals("Vivan", result.getPlayerName());
        assertEquals("", result.getPassword());  // because single-arg constructor is used

        // Verify DAO.savePlayer was called with the created player
        verify(playerDAO, times(1)).savePlayer(captor.capture());

        Player saved = captor.getValue();
        assertEquals("Vivan", saved.getPlayerName());
        assertEquals("", saved.getPassword());
    }

    @Test
    void testTrimWhitespace() {
        Player result = interactor.registerPlayer("   Alice   ");

        assertEquals("Alice", result.getPlayerName());
    }

    @Test
    void testEmptyNameThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> interactor.registerPlayer(""));
        assertThrows(IllegalArgumentException.class,
                () -> interactor.registerPlayer("   "));
        assertThrows(IllegalArgumentException.class,
                () -> interactor.registerPlayer(null));
    }
}
