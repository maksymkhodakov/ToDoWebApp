package volunteer.plus.todowebapp.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import volunteer.plus.todowebapp.domain.enumerated.TodoPriority;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, exclude = "user")
@Entity
@Table(name = "todo")
public class Todo extends BaseEntity {

    @Column(name = "task")
    private String task;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private TodoPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
