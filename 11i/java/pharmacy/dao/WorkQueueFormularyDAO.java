package inpatientWeb.pharmacy.dao;

import java.util.List;
import java.util.Map;

public interface WorkQueueFormularyDAO {
	int getAvailableMedCountForFacility(List<Integer> formularyIds, int facilityId);

	boolean isMedsAvailableForFacility(List<Integer> formularyIds, int facilityId);

	int getRoutedGenericItemIdByItemId(int itemId);

	Map<String, Object> getFormularyNotesByRoutedGenericItemId(int routedGenericItemId);

	Map<String, Object> getFormularyCommonSettingsByRoutedGenericItemId(long drugFormularyId);
}
