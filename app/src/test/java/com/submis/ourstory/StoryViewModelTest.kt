package com.submis.ourstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.submis.ourstory.dom.story.model.Story
import com.submis.ourstory.dom.auth.usecase.AuthUseCase
import com.submis.ourstory.dom.story.usecase.StoryUseCase
import com.submis.ourstory.ui.adapter.StoryAdapter
import com.submis.ourstory.ui.main.viewmodel.StoryViewModel
import com.submis.ourstory.utils.DataDummy
import com.submis.ourstory.utils.MainDispatcherRule
import com.submis.ourstory.utils.awaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyUseCase: StoryUseCase

    @Mock
    private lateinit var authUseCase: AuthUseCase

    @Test
    fun `verify to make sure get Story not null but returns correct data`() = runTest {
        val dummyStory = DataDummy.dummyEntityTest()
        val dataPaging = PagingData.from(dummyStory)
        val dataStory = MutableLiveData<PagingData<Story>>()
        dataStory.value = dataPaging

        `when`(storyUseCase.getStory()).thenReturn(flowOf(dataPaging))
        val vm = StoryViewModel(authUseCase, storyUseCase)

        val observedStory = vm.stories.awaitValue()
        val asyncDiffer = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        asyncDiffer.submitData(observedStory)

        assertNotNull(asyncDiffer.snapshot())
        assertEquals(dummyStory.size, asyncDiffer.snapshot().size)
        assertEquals(dummyStory[0], asyncDiffer.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Return No Data`() = runTest {
        val dataPaging = PagingData.from(emptyList<Story>())
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = dataPaging

        `when`(storyUseCase.getStory()).thenReturn(flowOf(dataPaging))
        val vm = StoryViewModel(authUseCase, storyUseCase)

        val actualStory = vm.stories.awaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}