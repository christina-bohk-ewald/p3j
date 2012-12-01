package p3j.pppm.parameters;

import p3j.pppm.SubPopulation;

public enum ParameterType {

  JUMP_OFF,

  MIGRATION,

  FERTILITY,

  MORTALITY,

  PROP_MALE_LIVE_BIRTHS,

  PROP_INF_DEATHS_FIRST_6M,

  SURV_PROB_OPEN_END;

  public static final String LABEL_DELIM = ": ";

  public static final String LABEL_JUMP_OFF_POPULATION = LABEL_DELIM
      + "Jump-off population";

  public static final String LABEL_FERTILITY = LABEL_DELIM + "Fertility";

  /**
   * The survivors at age x. Since this string is later used to check whether a
   * given parameter refers to mortality, it should be constant over all
   * {@link Parameter} instances.
   * 
   * TODO: Add an enumeration to define the type of a {@link Parameter}.
   */
  public static final String SURVIVORS_AGE_X = "Survivors at age x";

  public static final String LABEL_SURVIVORS_AGE_X = ": " + SURVIVORS_AGE_X;

  public static final String LABEL_PROPORTION_MALE_LIVE_BIRTHS = LABEL_DELIM
      + "Proportion of male live births";

  public static final String LABEL_PROPORTION_OF_INFANT_DEATHS_FIRST_6_MONTHS = LABEL_DELIM
      + "Proportion of infant deaths dying in the first 6 months";

  public static final String LABEL_SURVIVAL_PROBABILITY_OF_OPEN_END_AGE_CLASS = LABEL_DELIM
      + "Survival probability of open-end age class";

  public static final String LABEL_MALES = " (male)";

  public static final String LABEL_FEMALES = " (female)";

  public String getLabelFor(SubPopulation subPopulation) {
    String prefix = subPopulation.getName();
    switch (this) {
    case JUMP_OFF:
      return prefix + LABEL_JUMP_OFF_POPULATION;
    case MIGRATION:
      return prefix; // Special case in naming -- no additional label
    case FERTILITY:
      return prefix + LABEL_FERTILITY;
    case MORTALITY:
      return prefix + LABEL_SURVIVORS_AGE_X;
    case PROP_MALE_LIVE_BIRTHS:
      return prefix + LABEL_PROPORTION_MALE_LIVE_BIRTHS;
    case PROP_INF_DEATHS_FIRST_6M:
      return prefix + LABEL_PROPORTION_OF_INFANT_DEATHS_FIRST_6_MONTHS;
    case SURV_PROB_OPEN_END:
      return prefix + LABEL_SURVIVAL_PROBABILITY_OF_OPEN_END_AGE_CLASS;
    default:
      throw new IllegalArgumentException(
          "This type of parameter is not handled properly:" + this);
    }
  }

}
