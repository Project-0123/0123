import { AxiosError } from "axios";
import {
  QueryClient,
  UseQueryOptions,
  UseInfiniteQueryOptions,
  UseMutationOptions,
  DefaultOptions,
} from "@tanstack/react-query";

const queryConfig: DefaultOptions = {
  queries: {
    throwOnError: (error) => {
      if (error instanceof AxiosError)
        return error.response?.status ? error.response?.status >= 500 : true;
      else return true;
    },
    refetchOnWindowFocus: false,
    retry: false,
  },
};

export const queryClient = new QueryClient({ defaultOptions: queryConfig });

export type ExtractFnReturnType<FnType extends (...args: any) => any> = Awaited<
  ReturnType<FnType>
>;

export type QueryConfig<QueryFnType extends (...args: any) => any> = Omit<
  UseQueryOptions<ExtractFnReturnType<QueryFnType>>,
  "queryKey" | "queryFn"
>;

export type MutationConfig<MutationFnType extends (...args: any) => any> =
  UseMutationOptions<
    ExtractFnReturnType<MutationFnType>,
    AxiosError,
    Parameters<MutationFnType>[0]
  >;

export type InfiniteQueryConfig<QueryFnType extends (...args: any) => any> =
  UseInfiniteQueryOptions<
    ExtractFnReturnType<QueryFnType>,
    AxiosError,
    ExtractFnReturnType<QueryFnType>,
    ExtractFnReturnType<QueryFnType>,
    Array<string | Parameters<QueryFnType>[0]>,
    number
  >;
