package bes.max.features.main.domain.usecase

import bes.max.features.main.domain.repositories.CategoriesRepository
import bes.max.features.main.domain.repositories.SiteInfoRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoriesRepository,
    private val siteInfoRepository: SiteInfoRepository,
) {
    suspend operator fun invoke(category: Int) {
        categoryRepository.deleteByColor(category)
        siteInfoRepository.getByCategory(category).forEach { model ->
            siteInfoRepository.update(model.copy(categoryColor = null))
        }
    }
}