package software.bananen.gavel.model;

import org.junit.jupiter.api.Test;
import software.bananen.gavel.domain.model.WorkspaceNameValueObject;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.failBecauseExceptionWasNotThrown;

class WorkspaceNameValueObjectTest {

    @Test
    public void nameMayNotBeNull() {
        try {
            new WorkspaceNameValueObject(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("The value may not be null");
        }
    }

    @Test
    public void nameMayNotBeEmpty() {
        try {
            new WorkspaceNameValueObject("");
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("The value may not be empty");
        }
    }
}