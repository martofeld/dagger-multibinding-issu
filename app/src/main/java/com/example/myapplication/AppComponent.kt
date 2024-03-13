package com.example.myapplication

import dagger.Component
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider
import kotlin.reflect.KClass

@Component(
    modules = [
        MainModule::class
    ]
)
interface AppComponent {

    fun inject(app: App)

}

@Module(includes = [ExampleBinderModule::class, AnotherExampleBinderModule::class, OtherBinderModule::class])
class MainModule {
    @Provides
    fun provideRequestedObject(
        examples: Map<TopKey, @JvmSuppressWildcards Provider<ExampleInterface>>,
        examples2: Map<AnotherTopKey, @JvmSuppressWildcards Provider<ExampleInterface>>,
        others: Map<NestedKey, @JvmSuppressWildcards Provider<SecondInterface>>
    ) = RequestedObject(examples, examples2, others)
}

class RequestedObject(
    private val examples: Map<TopKey, Provider<ExampleInterface>>,
    private val examples2: Map<AnotherTopKey, Provider<ExampleInterface>>,
    private val others: Map<NestedKey, Provider<SecondInterface>>
) {
    init {
        examples.forEach { (key, value) ->
//            key.otherValues.map { others[it] } to value
        }
    }
}

@Module
class ExampleBinderModule {
    @[Provides IntoMap TopKey("example1")]
    fun provideExample1(): ExampleInterface {
        return Example1()
    }

    @[Provides IntoMap TopKey(
        "example2",
        otherValues = [NestedKey(Foo::class)]
    )]
    fun provideExample2(): ExampleInterface {
        return Example2()
    }

    @[Provides IntoMap TopKey(
        "example3",
        otherValues = [NestedKey(Zoo::class, identifier = Example3::class)]
    )]
    fun provideExample3(): ExampleInterface {
        return Example3()
    }
}

@Module
class AnotherExampleBinderModule {
    @[Provides IntoMap AnotherTopKey("example1")]
    fun provideExample1(): ExampleInterface {
        return Example1()
    }

    @[Provides IntoMap AnotherTopKey(
        "example2",
        otherValues = NestedKey(Foo::class)
    )]
    fun provideExample2(): ExampleInterface {
        return Example2()
    }

    @[Provides IntoMap AnotherTopKey(
        "example3",
        otherValues = NestedKey(Zoo::class, identifier = Example3::class)
    )]
    fun provideExample3(): ExampleInterface {
        return Example3()
    }
}

@Module
class OtherBinderModule {
    @[Provides IntoMap NestedKey(Foo::class)]
    fun provideFoo(): SecondInterface = Foo()

    @[Provides IntoMap NestedKey(Foo::class, identifier = Example2::class)]
    fun provideBar(): SecondInterface = Bar()
}

@MapKey(unwrapValue = false)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TopKey(
    val value: String,
    val otherValues: Array<NestedKey> = []
)

@MapKey(unwrapValue = false)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class AnotherTopKey(
    val value: String,
    val otherValues: NestedKey = NestedKey(SecondInterface::class)
)

@MapKey(unwrapValue = false)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class NestedKey(
    val value: KClass<out SecondInterface>,
    val identifier: KClass<out ExampleInterface> = DEFAULT::class
) {
    interface DEFAULT : ExampleInterface
}


interface ExampleInterface
class Example1 : ExampleInterface
class Example2 : ExampleInterface
class Example3 : ExampleInterface

interface SecondInterface
class Foo : SecondInterface
class Bar : SecondInterface
class Zoo : SecondInterface