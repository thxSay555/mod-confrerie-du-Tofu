package fr.skill;

import fr.skill.race.xelor.PhaseSkill;

public final class SkillDefinitions {
    public static void registerAll() {
        // autres skills...
        PhaseSkill.register();
      

        // ex : SkillDefinitions.register(SomeOtherSkill.register());
    }
}
