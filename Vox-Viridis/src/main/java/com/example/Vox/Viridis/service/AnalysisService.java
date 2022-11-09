package com.example.Vox.Viridis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.Vox.Viridis.model.Analysis;
import com.example.Vox.Viridis.model.Participation;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AnalysisService {
    private final ParticipationService participationService;

    public Analysis getAnalysis() {
        List<Participation> participation = participationService.getParticipation();

        Map<String, Integer> categoryMap = new HashMap<>();
        for (Participation p : participation) {
            String category = p.getReward().getOfferedBy().getCategory();
            if (categoryMap.containsKey(category)) {
                categoryMap.put(category, categoryMap.get(category) + 1);
            } else {
                categoryMap.put(category, 1);
            }
        }

        List<Integer> participationList = new ArrayList<>();
        List<String> category = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            category.add(entry.getKey());
            participationList.add(entry.getValue());
        }

        return new Analysis(participationList, category);

    }
}
