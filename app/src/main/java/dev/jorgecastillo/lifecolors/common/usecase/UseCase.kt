package dev.jorgecastillo.lifecolors.common.usecase

interface UseCase<I, O> {
  suspend fun execute(input: I): O
}

suspend fun <O> UseCase<Unit, O>.execute() = execute(Unit)
