package mtel.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mtel.model.Playbook;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostDTO {

    private String groupName;
    private String name;
    private List<Playbook> playbooks;


    public HostDTO(String groupName, String name) {
        this.groupName = groupName;
        this.name = name;
        this.playbooks = new ArrayList<>();
    }

    public HostDTO(String name) {
        this.name = name;
        this.playbooks = new ArrayList<>();
    }
}
